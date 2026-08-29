package com.taskflow.taskservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUserExists")
    public boolean userExists (String username) {
        Boolean exists = webClientBuilder.build()
        .get()
        .uri("http://USER-SERVICE/api/users/{username}/exists",username)
        .retrieve()
        .bodyToMono(Boolean.class)
        .block();
        return Boolean.TRUE.equals(exists);
    }

    // Fallback signature must match the original method's params, plus a Throwable at the end
    private boolean fallbackUserExists(String username, Throwable t) {
        // User Service is down, slow, or the breaker is open.
        // Fail safe: refuse the assignment rather than trust an unverified username.     
        return false;
    }
}
