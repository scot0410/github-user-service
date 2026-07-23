package com.branch.github_user_service.user.service;

import com.branch.github_user_service.user.client.GitHubClient;
import com.branch.github_user_service.user.exception.InvalidUsernameException;
import com.branch.github_user_service.user.model.GitHubApiRepoResponse;
import com.branch.github_user_service.user.model.GitHubApiUserResponse;
import com.branch.github_user_service.user.model.GitHubUser;
import com.branch.github_user_service.user.model.Repo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {
    private final GitHubClient client;

    @Cacheable("users")
    public GitHubUser getByUserName(String username) {
        validateInput(username);

        var userData = client.getUserData(username);
        var repoData = client.getRepoData(username);

        return buildGitHubUser(userData, repoData);
    }

    private static void validateInput(String username) {
        if (username.trim().isEmpty() || username.replace("\"", "").trim().isBlank()) {
            throw new InvalidUsernameException("Username cannot be empty or blank");
        }
    }

    private GitHubUser buildGitHubUser(GitHubApiUserResponse userData,
                                       List<GitHubApiRepoResponse> repoData) {

        var createdAtGmt = convertIsoToGmt(userData.createdAt());

        var repos = repoData.stream()
                .map(gitHubApiRepoResponse ->
                        Repo.builder()
                                .name(gitHubApiRepoResponse.name())
                                .url(gitHubApiRepoResponse.url())
                                .build())
                .toList();
        return GitHubUser.builder()
                .userName(userData.login())
                .displayName(userData.name())
                .avatar(userData.avatarUrl())
                .geoLocation(userData.location())
                .email(userData.email())
                .url(userData.url())
                .createdAt(createdAtGmt)
                .repos(repos)
                .build();
    }

    private String convertIsoToGmt(String s) {
        var RFC_1123_FORMATTER =
                DateTimeFormatter.RFC_1123_DATE_TIME
                        .withZone(ZoneId.of("GMT"))
                        .withLocale(Locale.US);

        Instant instant = Instant.parse(s);
        return RFC_1123_FORMATTER.format(instant);
    }

}

