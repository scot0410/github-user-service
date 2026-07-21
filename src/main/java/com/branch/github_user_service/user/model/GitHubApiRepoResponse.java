package com.branch.github_user_service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiRepoResponse(
        String name,
        @JsonProperty("html_url") String url
) {}