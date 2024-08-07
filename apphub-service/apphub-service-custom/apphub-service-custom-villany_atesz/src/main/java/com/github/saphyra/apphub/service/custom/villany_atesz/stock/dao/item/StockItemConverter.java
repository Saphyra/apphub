package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class StockItemConverter extends ConverterBase<StockItemEntity, StockItem> {
    static final String COLUMN_NAME = "name";
    static final String COLUMN_SERIAL_NUMBER = "serial_number";
    static final String COLUMN_IN_CAR = "in_car";
    static final String COLUMN_IN_STORAGE = "in_storage";
    static final String COLUMN_INVENTORIED = "inventoried";
    static final String COLUMN_BAR_CODE = "bar_code";

    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected StockItemEntity processDomainConversion(StockItem domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String stockId = uuidConverter.convertDomain(domain.getStockItemId());

        return StockItemEntity.builder()
            .stockItemId(stockId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .stockCategoryId(uuidConverter.convertDomain(domain.getStockCategoryId()))
            .name(stringEncryptor.encrypt(domain.getName(), userId, stockId, COLUMN_NAME))
            .serialNumber(stringEncryptor.encrypt(domain.getSerialNumber(), userId, stockId, COLUMN_SERIAL_NUMBER))
            .barCode(stringEncryptor.encrypt(domain.getBarCode(), userId, stockId, COLUMN_BAR_CODE))
            .inCar(integerEncryptor.encrypt(domain.getInCar(), userId, stockId, COLUMN_IN_CAR))
            .inStorage(integerEncryptor.encrypt(domain.getInStorage(), userId, stockId, COLUMN_IN_STORAGE))
            .inventoried(booleanEncryptor.encrypt(domain.isInventoried(), userId, stockId, COLUMN_INVENTORIED))
            .markedForAcquisition(domain.isMarkedForAcquisition())
            .build();
    }

    @Override
    protected StockItem processEntityConversion(StockItemEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return StockItem.builder()
            .stockItemId(uuidConverter.convertEntity(entity.getStockItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .stockCategoryId(uuidConverter.convertEntity(entity.getStockCategoryId()))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getStockItemId(), COLUMN_NAME))
            .serialNumber(stringEncryptor.decrypt(entity.getSerialNumber(), userId, entity.getStockItemId(), COLUMN_SERIAL_NUMBER))
            .barCode(stringEncryptor.decrypt(entity.getBarCode(), userId, entity.getStockItemId(), COLUMN_BAR_CODE))
            .inCar(integerEncryptor.decrypt(entity.getInCar(), userId, entity.getStockItemId(), COLUMN_IN_CAR))
            .inStorage(integerEncryptor.decrypt(entity.getInStorage(), userId, entity.getStockItemId(), COLUMN_IN_STORAGE))
            .inventoried(Optional.ofNullable(entity.getInventoried()).map(inventoried -> booleanEncryptor.decrypt(inventoried, userId, entity.getStockItemId(), COLUMN_INVENTORIED)).orElse(false))
            .markedForAcquisition(entity.getMarkedForAcquisition())
            .build();
    }
}
