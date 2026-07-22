package com.branch.github_user_service.user.service;

import com.branch.github_user_service.user.client.GitHubClient;
import com.branch.github_user_service.user.exception.UserNotFoundException;
import com.branch.github_user_service.user.model.GitHubApiRepoResponse;
import com.branch.github_user_service.user.model.GitHubApiUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private GitHubClient client;

    @InjectMocks
    private UserService service;

    @Test
    void getByUserNameShouldReturnGithubUser() {
        var USER_NAME = "octocat";

        var gitHubApiUserResponse = getGitHubApiUserResponse();
        var gitHubApiRepoResponse = getGitHubApiRepoResponse();

        when(client.getUserData(USER_NAME)).thenReturn(gitHubApiUserResponse);
        when(client.getRepoData(USER_NAME)).thenReturn(gitHubApiRepoResponse);

        var response = service.getByUserName(USER_NAME);

        assertThat(response, is(notNullValue()));
        assertThat(response.userName(), is(gitHubApiUserResponse.login()));
        assertThat(response.displayName(), is(gitHubApiUserResponse.name()));
        assertThat(response.avatar(), is(gitHubApiUserResponse.avatarUrl()));
        assertThat(response.geoLocation(), is(gitHubApiUserResponse.location()));
        assertThat(response.email(), is(gitHubApiUserResponse.email()));
        assertThat(response.url(), is(gitHubApiUserResponse.url()));
        assertThat(response.createdAt(), is("Tue, 25 Jan 2011 18:44:36 GMT"));

        assertThat(response.repos().size(), is(2));
        assertThat(response.repos().getFirst().name(), is("boysenberry-repo-1"));
        assertThat(response.repos().getFirst().url(), is("https://github.com/octocat/boysenberry-repo-1"));
        assertThat(response.repos().getLast().name(), is("git-consortium"));
        assertThat(response.repos().getLast().url(), is("https://github.com/octocat/git-consortium"));
    }

    @Test
    void getByUserNameShouldThrowUserNotFoundExceptionWhenUserMissing() {
        var USER_NAME = "octocat";

        when(client.getUserData(USER_NAME)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.getByUserName(USER_NAME));

        verify(client, never()).getRepoData(USER_NAME);
    }

    private GitHubApiUserResponse getGitHubApiUserResponse() {
        return new GitHubApiUserResponse(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                "2011-01-25T18:44:36Z"
        );
    }

    private List<GitHubApiRepoResponse> getGitHubApiRepoResponse() {
        var repo1 = new GitHubApiRepoResponse(
                "boysenberry-repo-1",
                "https://github.com/octocat/boysenberry-repo-1"
        );

        var repo2 = new GitHubApiRepoResponse(
                "git-consortium",
                "https://github.com/octocat/git-consortium"
        );

        return List.of(repo1, repo2);
    }

}
