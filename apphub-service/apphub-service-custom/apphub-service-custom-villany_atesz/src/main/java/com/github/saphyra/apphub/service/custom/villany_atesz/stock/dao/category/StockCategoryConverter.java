package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

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
class StockCategoryConverter extends ConverterBase<StockCategoryEntity, StockCategory> {
    static final String COLUMN_NAME = "name";
    static final String COLUMN_MEASUREMENT = "measurement";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected StockCategoryEntity processDomainConversion(StockCategory domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String categoryId = uuidConverter.convertDomain(domain.getStockCategoryId());

        return StockCategoryEntity.builder()
            .stockCategoryId(categoryId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .name(stringEncryptor.encrypt(domain.getName(), userId, categoryId, COLUMN_NAME))
            .measurement(stringEncryptor.encrypt(domain.getMeasurement(), userId, categoryId, COLUMN_MEASUREMENT))
            .build();
    }

    @Override
    protected StockCategory processEntityConversion(StockCategoryEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return StockCategory.builder()
            .stockCategoryId(uuidConverter.convertEntity(entity.getStockCategoryId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getStockCategoryId(), COLUMN_NAME))
            .measurement(stringEncryptor.decrypt(entity.getMeasurement(), userId, entity.getStockCategoryId(), COLUMN_MEASUREMENT))
            .build();
    }
}
