package com.cfd.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.user.domain.InMemoryUserRepository;
import com.cfd.user.domain.UserRepository;

@Configuration
public class UserModuleConfiguration {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }
}
