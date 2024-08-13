package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ToolTypeConverter extends ConverterBase<ToolTypeEntity, ToolType> {
    static final String COLUMN_NAME = "name";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected ToolTypeEntity processDomainConversion(ToolType domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String storageBoxId = uuidConverter.convertDomain(domain.getToolTypeId());

        return ToolTypeEntity.builder()
            .toolTypeId(storageBoxId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .name(stringEncryptor.encrypt(domain.getName(), userId, storageBoxId, COLUMN_NAME))
            .build();
    }

    @Override
    protected ToolType processEntityConversion(ToolTypeEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return ToolType.builder()
            .toolTypeId(uuidConverter.convertEntity(entity.getToolTypeId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getToolTypeId(), COLUMN_NAME))
            .build();
    }
}
