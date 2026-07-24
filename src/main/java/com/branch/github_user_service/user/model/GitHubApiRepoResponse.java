package com.branch.github_user_service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiRepoResponse(
        String name,
        String url
) {}


