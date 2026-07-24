package com.branch.github_user_service.user.controller;

import com.branch.github_user_service.user.exception.UserNotFoundException;
import com.branch.github_user_service.user.model.GitHubUser;
import com.branch.github_user_service.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserApiTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserApi userApi;

    @Test
    void getByUserNameShouldReturnGitHubUser() {
        var USER_NAME = "octocat";
        var githubUserMock = mock(GitHubUser.class);
        when(userService.getByUserName(USER_NAME)).thenReturn(githubUserMock);
        var response = userApi.getByUserName("octocat");

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().size(), is(0));
        assertThat(response.getBody(), is(githubUserMock));
    }

    @Test
    void getByUserNameShouldThrowUserNotFoundExceptionWhenUserMissing() {
        var USER_NAME = "octocat";
        when(userService.getByUserName(USER_NAME)).thenThrow(new UserNotFoundException("User not found"));

        var ex = assertThrows(UserNotFoundException.class, () -> userApi.getByUserName("octocat"));

        assertThat(ex.getMessage(), is("User not found"));
    }

}
