package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ErrorReportConverter extends ConverterBase<ErrorReportEntity, ErrorReport> {
    private final UuidConverter uuidConverter;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected ErrorReport processEntityConversion(ErrorReportEntity entity) {
        return ErrorReport.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .createdAt(entity.getCreatedAt())
            .message(entity.getMessage())
            .responseStatus(entity.getResponseStatus())
            .responseBody(entity.getResponseBody())
            .exception(objectMapperWrapper.readValue(entity.getException(), ExceptionModel.class))
            .status(ErrorReportStatus.valueOf(entity.getStatus()))
            .service(entity.getService())
            .build();
    }

    @Override
    protected ErrorReportEntity processDomainConversion(ErrorReport domain) {
        return ErrorReportEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .createdAt(domain.getCreatedAt())
            .message(domain.getMessage())
            .responseStatus(domain.getResponseStatus())
            .responseBody(domain.getResponseBody())
            .exception(objectMapperWrapper.writeValueAsString(domain.getException()))
            .status(domain.getStatus().name())
            .service(domain.getService())
            .build();
    }
}
