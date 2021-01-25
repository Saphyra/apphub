package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class LogParameterVisibilityConverter extends ConverterBase<LogParameterVisibilityEntity, LogParameterVisibility> {
    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final UuidConverter uuidConverter;

    @Override
    protected LogParameterVisibility processEntityConversion(LogParameterVisibilityEntity entity) {
        return LogParameterVisibility.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parameter(stringEncryptor.decryptEntity(entity.getParameter(), entity.getUserId()))
            .visible(booleanEncryptor.decryptEntity(entity.getVisible(), entity.getUserId()))
            .build();
    }

    @Override
    protected LogParameterVisibilityEntity processDomainConversion(LogParameterVisibility domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return LogParameterVisibilityEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .userId(userId)
            .parameter(stringEncryptor.encryptEntity(domain.getParameter(), userId))
            .visible(booleanEncryptor.encryptEntity(domain.isVisible(), userId))
            .build();
    }
}
