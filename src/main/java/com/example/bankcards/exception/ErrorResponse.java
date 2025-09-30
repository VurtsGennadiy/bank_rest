package com.example.bankcards.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сведения об ошибке")
public class ErrorResponse {
    @Schema(description = "Описание ошибки", example = "error message")
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}