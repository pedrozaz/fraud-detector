package io.github.pedrozaz.api.dto;

public record RegisterRequest(
        String username,
        String password
) {
}
