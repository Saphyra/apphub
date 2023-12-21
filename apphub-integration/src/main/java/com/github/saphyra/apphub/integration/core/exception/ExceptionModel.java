package com.github.saphyra.apphub.integration.core.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExceptionModel {
    private String message;
    private String type;
    private String thread;
    private List<String> stackTrace;
    private ExceptionModel cause;
}
