package com.github.saphyra.apphub.integration.core.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExceptionConverter {
    public static ExceptionModel map(Throwable exception) {
        return ExceptionModel.builder()
            .message(exception.getMessage())
            .type(exception.getClass().getTypeName())
            .thread(Thread.currentThread().getName())
            .stackTrace(mapStackTrace(exception.getStackTrace()))
            .cause(Optional.ofNullable(exception.getCause()).map(ExceptionConverter::map).orElse(null))
            .build();
    }

    private static List<String> mapStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
            .map(stackTraceElement -> String.format(
                "at %s.%s(%s:%s)",
                stackTraceElement.getClassName(),
                stackTraceElement.getMethodName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber()
            ))
            .collect(Collectors.toList());
    }
}
