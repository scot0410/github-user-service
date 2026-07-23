package com.branch.github_user_service.user.model;

import lombok.Builder;

@Builder
public record Repo(
        String name,
        String url
) {}

