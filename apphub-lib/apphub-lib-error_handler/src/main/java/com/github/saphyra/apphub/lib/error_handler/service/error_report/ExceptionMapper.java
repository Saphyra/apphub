package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class ExceptionMapper {
    private final StackTraceMapper stackTraceMapper;

    ExceptionModel map(Throwable exception) {
        return ExceptionModel.builder()
            .message(exception.getMessage())
            .type(exception.getClass().getTypeName())
            .thread(Thread.currentThread().getName())
            .stackTrace(stackTraceMapper.mapStackTrace(exception.getStackTrace()))
            .cause(Optional.ofNullable(exception.getCause()).map(this::map).orElse(null))
            .build();
    }
}
