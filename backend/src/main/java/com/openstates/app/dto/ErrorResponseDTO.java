package com.openstates.app.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        LocalDateTime timestamp
) {}
