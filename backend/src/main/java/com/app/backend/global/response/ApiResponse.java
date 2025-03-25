package com.app.backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @NotNull
    private final boolean isSuccess;

    @NotNull
    private final String code;

    @NotNull
    private final String message;

    private final T data;

    public static <T> ApiResponse<T> of(@NotNull final Boolean isSuccess,
                                        @NotNull final String code,
                                        @NotNull final String message) {
        return new ApiResponse<>(isSuccess, code, message, null);
    }

    public static <T> ApiResponse<T> of(@NotNull final Boolean isSuccess,
                                        @NotNull final HttpStatus status,
                                        @NotNull final String message) {
        return new ApiResponse<>(isSuccess, String.valueOf(status.value()), message, null);
    }

    public static <T> ApiResponse<T> of(@NotNull final Boolean isSuccess,
                                        @NotNull final String code,
                                        @NotNull final String message,
                                        @NotNull final T data) {
        return new ApiResponse<>(isSuccess, code, message, data);
    }

    public static <T> ApiResponse<T> of(@NotNull final Boolean isSuccess,
                                        @NotNull final HttpStatus status,
                                        @NotNull final String message,
                                        @NotNull final T data) {
        return new ApiResponse<>(isSuccess, String.valueOf(status.value()), message, data);
    }
}
