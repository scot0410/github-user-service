package com.branch.github_user_service.user.model;

import lombok.Builder;

import java.util.List;

@Builder
public record GitHubUser(
        String userName,
        String displayName,
        String avatar,
        String geoLocation,
        String email,
        String url,
        String createdAt,
        List<Repo> repos
) {}
