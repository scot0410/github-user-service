package com.branch.github_user_service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiUserResponse(
        String login,
        String name,
        @JsonProperty("avatar_url") String avatarUrl,
        String location,
        String email,//can be null
        String url,
        @JsonProperty("created_at") String createdAt
) {}

