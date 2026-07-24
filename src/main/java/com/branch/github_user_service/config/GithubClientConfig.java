package com.branch.github_user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GithubClientConfig {

    @Bean
    public RestClient githubClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.github.com")
                .build();
    }
}

