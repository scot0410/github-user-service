package com.branch.github_user_service.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record GitHubUser(
        @JsonProperty("user_name") String userName,
        @JsonProperty("display_name")String displayName,
        String avatar,
        @JsonProperty("geo_location") String geoLocation,
        String email,
        String url,
        @JsonProperty("created_at") String createdAt,
        List<Repo> repos
) {}

