package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StockItemPriceConverter extends ConverterBase<StockItemPriceEntity, StockItemPrice> {
    static final String COLUMN_PRICE = "price";

    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected StockItemPriceEntity processDomainConversion(StockItemPrice domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String stockItemPriceId = uuidConverter.convertDomain(domain.getStockItemPriceId());

        return StockItemPriceEntity.builder()
            .stockItemPriceId(stockItemPriceId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .stockItemId(uuidConverter.convertDomain(domain.getStockItemId()))
            .price(integerEncryptor.encrypt(domain.getPrice(), userId, stockItemPriceId, COLUMN_PRICE))
            .build();
    }

    @Override
    protected StockItemPrice processEntityConversion(StockItemPriceEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return StockItemPrice.builder()
            .stockItemPriceId(uuidConverter.convertEntity(entity.getStockItemPriceId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .stockItemId(uuidConverter.convertEntity(entity.getStockItemId()))
            .price(integerEncryptor.decrypt(entity.getPrice(), userId, entity.getStockItemPriceId(), COLUMN_PRICE))
            .build();
    }
}
