package com.branch.github_user_service.user.controller;

import com.branch.github_user_service.user.model.GitHubUser;
import com.branch.github_user_service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @GetMapping("/users/{username}")
    ResponseEntity<GitHubUser> getByUserName(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }
}

