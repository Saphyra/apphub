package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryConverter.COLUMN_MEASUREMENT;
import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryConverter.COLUMN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockCategoryConverterTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String MEASUREMENT = "measurement";
    private static final String USER_ID_IN_ACCESS_TOKEN = "user-id-in-access-token";
    private static final String USER_ID_STRING = "user-id";
    private static final String STOCK_CATEGORY_ID_STRING = "stock-category-id";
    private static final String ENCRYPTED_NAME = "encrypted-name";
    private static final String ENCRYPTED_MEASUREMENT = "encrypted-measurement";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private StockCategoryConverter underTest;

    @Test
    void convertDomain() {
        StockCategory domain = StockCategory.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .userId(USER_ID)
            .name(NAME)
            .measurement(MEASUREMENT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_CATEGORY_ID)).willReturn(STOCK_CATEGORY_ID_STRING);
        given(stringEncryptor.encrypt(NAME, USER_ID_IN_ACCESS_TOKEN, STOCK_CATEGORY_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);
        given(stringEncryptor.encrypt(MEASUREMENT, USER_ID_IN_ACCESS_TOKEN, STOCK_CATEGORY_ID_STRING, COLUMN_MEASUREMENT)).willReturn(ENCRYPTED_MEASUREMENT);

        assertThat(underTest.convertDomain(domain))
            .returns(STOCK_CATEGORY_ID_STRING, StockCategoryEntity::getStockCategoryId)
            .returns(USER_ID_STRING, StockCategoryEntity::getUserId)
            .returns(ENCRYPTED_NAME, StockCategoryEntity::getName)
            .returns(ENCRYPTED_MEASUREMENT, StockCategoryEntity::getMeasurement);
    }

    @Test
    void convertEntity() {
        StockCategoryEntity entity = StockCategoryEntity.builder()
            .stockCategoryId(STOCK_CATEGORY_ID_STRING)
            .userId(USER_ID_STRING)
            .name(ENCRYPTED_NAME)
            .measurement(ENCRYPTED_MEASUREMENT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STOCK_CATEGORY_ID_STRING)).willReturn(STOCK_CATEGORY_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_IN_ACCESS_TOKEN, STOCK_CATEGORY_ID_STRING, COLUMN_NAME)).willReturn(NAME);
        given(stringEncryptor.decrypt(ENCRYPTED_MEASUREMENT, USER_ID_IN_ACCESS_TOKEN, STOCK_CATEGORY_ID_STRING, COLUMN_MEASUREMENT)).willReturn(MEASUREMENT);

        assertThat(underTest.convertEntity(entity))
            .returns(STOCK_CATEGORY_ID, StockCategory::getStockCategoryId)
            .returns(USER_ID, StockCategory::getUserId)
            .returns(NAME, StockCategory::getName)
            .returns(MEASUREMENT, StockCategory::getMeasurement);
    }
}