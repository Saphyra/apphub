package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.StackTraceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StackTraceMapper {
    List<StackTraceModel> mapStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
            .map(stackTraceElement -> StackTraceModel.builder()
                .fileName(stackTraceElement.getFileName())
                .className(stackTraceElement.getClassName())
                .methodName(stackTraceElement.getMethodName())
                .lineNumber(stackTraceElement.getLineNumber())
                .build())
            .collect(Collectors.toList());
    }
}
