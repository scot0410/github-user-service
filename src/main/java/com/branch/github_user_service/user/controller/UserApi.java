package com.branch.github_user_service.user.controller;

import com.branch.github_user_service.user.model.GitHubUser;
import com.branch.github_user_service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @GetMapping("/users")
    ResponseEntity<GitHubUser> getByUserName(@RequestParam(name = "username")
                                             String username) {
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }
}

