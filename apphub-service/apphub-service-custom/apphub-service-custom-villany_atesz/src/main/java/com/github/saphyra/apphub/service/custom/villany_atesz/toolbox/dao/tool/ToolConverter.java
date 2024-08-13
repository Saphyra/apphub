package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
class ToolConverter extends ConverterBase<ToolEntity, Tool> {
    static final String COLUMN_BRAND = "brand";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_COST = "cost";
    static final String COLUMN_ACQUIRED_AT = "acquired_at";
    static final String COLUMN_WARRANTY_EXPIRES_AT = "warranty_expires_at";
    static final String COLUMN_SCRAPPED_AT = "scrapped_at";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final LocalDateEncryptor localDateEncryptor;

    @Override
    protected ToolEntity processDomainConversion(Tool domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String toolId = uuidConverter.convertDomain(domain.getToolId());

        return ToolEntity.builder()
            .toolId(toolId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .storageBoxId(uuidConverter.convertDomain(domain.getStorageBoxId()))
            .toolTypeId(uuidConverter.convertDomain(domain.getToolTypeId()))
            .brand(stringEncryptor.encrypt(domain.getBrand(), userId, toolId, COLUMN_BRAND))
            .name(stringEncryptor.encrypt(domain.getName(), userId, toolId, COLUMN_NAME))
            .cost(integerEncryptor.encrypt(domain.getCost(), userId, toolId, COLUMN_COST))
            .acquiredAt(localDateEncryptor.encrypt(domain.getAcquiredAt(), userId, toolId, COLUMN_ACQUIRED_AT))
            .warrantyExpiresAt(localDateEncryptor.encrypt(domain.getWarrantyExpiresAt(), userId, toolId, COLUMN_WARRANTY_EXPIRES_AT))
            .status(domain.getStatus())
            .scrappedAt(localDateEncryptor.encrypt(domain.getScrappedAt(), userId, toolId, COLUMN_SCRAPPED_AT))
            .build();
    }

    @Override
    protected Tool processEntityConversion(ToolEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return Tool.builder()
            .toolId(uuidConverter.convertEntity(entity.getToolId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .storageBoxId(uuidConverter.convertEntity(entity.getStorageBoxId()))
            .toolTypeId(uuidConverter.convertEntity(entity.getToolTypeId()))
            .brand(stringEncryptor.decrypt(entity.getBrand(), userId, entity.getToolId(), COLUMN_BRAND))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getToolId(), COLUMN_NAME))
            .cost(integerEncryptor.decrypt(entity.getCost(), userId, entity.getToolId(), COLUMN_COST))
            .acquiredAt(localDateEncryptor.decrypt(entity.getAcquiredAt(), userId, entity.getToolId(), COLUMN_ACQUIRED_AT))
            .warrantyExpiresAt(localDateEncryptor.decrypt(entity.getWarrantyExpiresAt(), userId, entity.getToolId(), COLUMN_WARRANTY_EXPIRES_AT))
            .status(entity.getStatus())
            .scrappedAt(localDateEncryptor.decrypt(entity.getScrappedAt(), userId, entity.getToolId(), COLUMN_SCRAPPED_AT))
            .build();
    }
}
