package com.branch.github_user_service.user.client;

import com.branch.github_user_service.config.GithubClientConfig;
import com.branch.github_user_service.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GitHubClient.class)
@Import(GithubClientConfig.class)
public class GitHubClientTest {
    @Autowired
    GitHubClient client;

    @Autowired
    MockRestServiceServer mockServer;

    @Test
    @DisplayName("Should fetch and transform user data, ignoring unreferenced fields")
    void shouldFetchAndTransform() {
        String userName = "octocat";
        String mockUserJson = """
            {
                "login": "octocat",
                "name": "The Octocat",
                "avatar_url": "https://githubusercontent.com",
                "location": "San Francisco",
                "email": "blah@github.com",
                "url": "https://api.github.com/users/octocat",
                "created_at": "2011-01-25T18:44:36Z",
                "followers_url": "https://api.github.com/users/octocat/followers"
            }
            """;

        mockServer.expect(requestTo("https://api.github.com/users/octocat"))
                .andRespond(withSuccess(mockUserJson, MediaType.APPLICATION_JSON));

        var response = client.getUserData(userName);

        assertThat(response, is(notNullValue()));
        assertThat(response.login(), is("octocat"));
        assertThat(response.name(), is("The Octocat"));
        assertThat(response.avatarUrl(), is("https://githubusercontent.com"));
        assertThat(response.location(), is("San Francisco"));
        assertThat(response.email(), is("blah@github.com"));
        assertThat(response.url(), is("https://api.github.com/users/octocat"));
        assertThat(response.createdAt(), is("2011-01-25T18:44:36Z"));
    }

    @Test
    @DisplayName("Should successfully fetch and deserialize repository list with chosen fields")
    void shouldFetchRepositoriesSuccessfully() {
        String mockReposJson = """
            [
                {
                    "name": "boysenberry-repo-1",
                    "html_url": "https://github.com/boysenberry-repo-1",
                    "id": 12345
                },
                {
                    "name": "git-consortium",
                    "html_url": "https://github.com/git-consortium",
                    "id": 67890
                }
            ]
            """;

        this.mockServer.expect(requestTo("https://api.github.com/users/octocat/repos"))
                .andRespond(withSuccess(mockReposJson, MediaType.APPLICATION_JSON));

        var response = client.getRepoData("octocat");

        assertThat(response, is(notNullValue()));
        assertThat(response.size(), is(2));
        assertThat(response.getFirst().name(), is("boysenberry-repo-1"));
        assertThat(response.getFirst().url(), is("https://github.com/boysenberry-repo-1"));
        assertThat(response.getLast().name(), is("git-consortium"));
        assertThat(response.getLast().url(), is("https://github.com/git-consortium"));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when GitHub user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
        this.mockServer.expect(requestTo("https://api.github.com/users/unknown-user"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var exception = assertThrows(UserNotFoundException.class, () -> {
            client.getUserData("unknown-user");
        });

        assertThat(exception.getMessage(), is("GitHub user 'unknown-user' does not exist"));
    }

}
