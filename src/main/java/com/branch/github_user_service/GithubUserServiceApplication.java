package com.branch.github_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GithubUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubUserServiceApplication.class, args);
	}

}

