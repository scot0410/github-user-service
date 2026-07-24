package com.branch.github_user_service.user.client;

import com.branch.github_user_service.user.exception.GitHubApiException;
import com.branch.github_user_service.user.exception.UserNotFoundException;
import com.branch.github_user_service.user.model.GitHubApiRepoResponse;
import com.branch.github_user_service.user.model.GitHubApiUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubClient {
    private final RestClient githubClient;

    public GitHubApiUserResponse getUserData(String username){
        return githubClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new UserNotFoundException("GitHub user '%s' does not exist".formatted(username));
                    }
                    throw new GitHubApiException("GitHub client error encountered: %s".formatted(response.getStatusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new GitHubApiException("GitHub client error encountered: %s".formatted(response.getStatusCode()));
                })
                .body(GitHubApiUserResponse.class);
    }

    public List<GitHubApiRepoResponse> getRepoData(String username){
        return githubClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new UserNotFoundException("GitHub user '%s' does not exist".formatted(username));
                    }
                    throw new GitHubApiException("GitHub client error encountered: %s".formatted(response.getStatusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new GitHubApiException("GitHub client error encountered: %s".formatted(response.getStatusCode()));
                })
                .body(new ParameterizedTypeReference<List<GitHubApiRepoResponse>>() {});
    }
}
