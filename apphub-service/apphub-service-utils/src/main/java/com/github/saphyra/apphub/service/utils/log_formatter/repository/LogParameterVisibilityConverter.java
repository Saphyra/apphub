package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class LogParameterVisibilityConverter extends ConverterBase<LogParameterVisibilityEntity, LogParameterVisibility> {
    static final String COLUMN_PARAMETER = "parameter";
    static final String COLUMN_VISIBLE = "visible";

    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected LogParameterVisibility processEntityConversion(LogParameterVisibilityEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return LogParameterVisibility.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parameter(stringEncryptor.decrypt(entity.getParameter(), userId, entity.getId(), COLUMN_PARAMETER))
            .visible(booleanEncryptor.decrypt(entity.getVisible(), userId, entity.getId(), COLUMN_VISIBLE))
            .build();
    }

    @Override
    protected LogParameterVisibilityEntity processDomainConversion(LogParameterVisibility domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String id = uuidConverter.convertDomain(domain.getId());
        return LogParameterVisibilityEntity.builder()
            .id(id)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parameter(stringEncryptor.encrypt(domain.getParameter(), userId, id, COLUMN_PARAMETER))
            .visible(booleanEncryptor.encrypt(domain.isVisible(), userId, id, COLUMN_VISIBLE))
            .build();
    }
}
