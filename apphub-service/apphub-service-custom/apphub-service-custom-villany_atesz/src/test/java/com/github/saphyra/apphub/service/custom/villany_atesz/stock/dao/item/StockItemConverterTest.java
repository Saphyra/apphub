package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemConverter.COLUMN_INVENTORIED;
import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemConverter.COLUMN_IN_CAR;
import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemConverter.COLUMN_IN_STORAGE;
import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemConverter.COLUMN_NAME;
import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemConverter.COLUMN_SERIAL_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockItemConverterTest {
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final int IN_CAR = 34;
    private static final int IN_STORAGE = 45;
    private static final String USER_ID_IN_ACCESS_TOKEN = "user-id-in-access-token";
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String STOCK_CATEGORY_ID_STRING = "stock-category-id";
    private static final String ENCRYPTED_NAME = "encrypted-name";
    private static final String ENCRYPTED_SERIAL_NUMBER = "encrypted-serial-number";
    private static final String ENCRYPTED_IN_CAR = "encrypted-in-car";
    private static final String ENCRYPTED_IN_STORAGE = "encrypted-in-storage";
    private static final String ENCRYPTED_INVENTORIED = "encrypted-inventoried";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @InjectMocks
    private StockItemConverter underTest;

    @Test
    void convertDomain() {
        StockItem domain = StockItem.builder()
            .stockItemId(STOCK_ITEM_ID)
            .userId(USER_ID)
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .serialNumber(SERIAL_NUMBER)
            .inCar(IN_CAR)
            .inStorage(IN_STORAGE)
            .inventoried(true)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_CATEGORY_ID)).willReturn(STOCK_CATEGORY_ID_STRING);
        given(stringEncryptor.encrypt(NAME, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);
        given(stringEncryptor.encrypt(SERIAL_NUMBER, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_SERIAL_NUMBER)).willReturn(ENCRYPTED_SERIAL_NUMBER);
        given(integerEncryptor.encrypt(IN_CAR, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_IN_CAR)).willReturn(ENCRYPTED_IN_CAR);
        given(integerEncryptor.encrypt(IN_STORAGE, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_IN_STORAGE)).willReturn(ENCRYPTED_IN_STORAGE);
        given(booleanEncryptor.encrypt(true, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_INVENTORIED)).willReturn(ENCRYPTED_INVENTORIED);

        assertThat(underTest.convertDomain(domain))
            .returns(STOCK_ITEM_ID_STRING, StockItemEntity::getStockItemId)
            .returns(USER_ID_STRING, StockItemEntity::getUserId)
            .returns(STOCK_CATEGORY_ID_STRING, StockItemEntity::getStockCategoryId)
            .returns(ENCRYPTED_NAME, StockItemEntity::getName)
            .returns(ENCRYPTED_SERIAL_NUMBER, StockItemEntity::getSerialNumber)
            .returns(ENCRYPTED_IN_CAR, StockItemEntity::getInCar)
            .returns(ENCRYPTED_IN_STORAGE, StockItemEntity::getInStorage)
            .returns(ENCRYPTED_INVENTORIED, StockItemEntity::getInventoried);
    }

    @Test
    void convertEntity() {
        StockItemEntity domain = StockItemEntity.builder()
            .stockItemId(STOCK_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .stockCategoryId(STOCK_CATEGORY_ID_STRING)
            .name(ENCRYPTED_NAME)
            .serialNumber(ENCRYPTED_SERIAL_NUMBER)
            .inCar(ENCRYPTED_IN_CAR)
            .inStorage(ENCRYPTED_IN_STORAGE)
            .inventoried(ENCRYPTED_INVENTORIED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(STOCK_ITEM_ID_STRING)).willReturn(STOCK_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STOCK_CATEGORY_ID_STRING)).willReturn(STOCK_CATEGORY_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_NAME)).willReturn(NAME);
        given(stringEncryptor.decrypt(ENCRYPTED_SERIAL_NUMBER, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_SERIAL_NUMBER)).willReturn(SERIAL_NUMBER);
        given(integerEncryptor.decrypt(ENCRYPTED_IN_CAR, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_IN_CAR)).willReturn(IN_CAR);
        given(integerEncryptor.decrypt(ENCRYPTED_IN_STORAGE, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_IN_STORAGE)).willReturn(IN_STORAGE);
        given(booleanEncryptor.decrypt(ENCRYPTED_INVENTORIED, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_ID_STRING, COLUMN_INVENTORIED)).willReturn(true);

        assertThat(underTest.convertEntity(domain))
            .returns(STOCK_ITEM_ID, StockItem::getStockItemId)
            .returns(USER_ID, StockItem::getUserId)
            .returns(STOCK_CATEGORY_ID, StockItem::getStockCategoryId)
            .returns(NAME, StockItem::getName)
            .returns(SERIAL_NUMBER, StockItem::getSerialNumber)
            .returns(IN_CAR, StockItem::getInCar)
            .returns(IN_STORAGE, StockItem::getInStorage)
            .returns(true, StockItem::isInventoried);
    }
}