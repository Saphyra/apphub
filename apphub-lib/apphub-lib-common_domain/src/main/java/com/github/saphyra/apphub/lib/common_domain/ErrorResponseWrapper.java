package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponseWrapper {
    @NonNull
    private final ErrorResponse errorResponse;

    @NonNull
    private final HttpStatus status;
}
