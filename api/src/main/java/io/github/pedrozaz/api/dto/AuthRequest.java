package io.github.pedrozaz.api.dto;

public record AuthRequest(
        String username,
        String password
) {
}
