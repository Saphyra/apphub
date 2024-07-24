package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionConverter.COLUMN_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AcquisitionConverterTest {
    private static final UUID ACQUISITION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final int AMOUNT = 32;
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ACQUISITION_ID_STRING = "acquisition-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final String ENCRYPTED_AMOUNT = "encrypted-amount";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @InjectMocks
    private AcquisitionConverter underTest;

    @Test
    void convertDomain() {
        Acquisition acquisition = Acquisition.builder()
            .acquisitionId(ACQUISITION_ID)
            .userId(USER_ID)
            .acquiredAt(ACQUIRED_AT)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(ACQUISITION_ID)).willReturn(ACQUISITION_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(integerEncryptor.encrypt(AMOUNT, USER_ID_FROM_ACCESS_TOKEN, ACQUISITION_ID_STRING, COLUMN_AMOUNT)).willReturn(ENCRYPTED_AMOUNT);

        assertThat(underTest.convertDomain(acquisition))
            .returns(ACQUISITION_ID_STRING, AcquisitionEntity::getAcquisitionId)
            .returns(USER_ID_STRING, AcquisitionEntity::getUserId)
            .returns(ACQUIRED_AT.toString(), AcquisitionEntity::getAcquiredAt)
            .returns(STOCK_ITEM_ID_STRING, AcquisitionEntity::getStockItemId)
            .returns(ENCRYPTED_AMOUNT, AcquisitionEntity::getAmount);
    }

    @Test
    void convertEntity() {
        AcquisitionEntity acquisition = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_STRING)
            .userId(USER_ID_STRING)
            .acquiredAt(ACQUIRED_AT.toString())
            .stockItemId(STOCK_ITEM_ID_STRING)
            .amount(ENCRYPTED_AMOUNT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(ACQUISITION_ID_STRING)).willReturn(ACQUISITION_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STOCK_ITEM_ID_STRING)).willReturn(STOCK_ITEM_ID);
        given(integerEncryptor.decrypt(ENCRYPTED_AMOUNT, USER_ID_FROM_ACCESS_TOKEN, ACQUISITION_ID_STRING, COLUMN_AMOUNT)).willReturn(AMOUNT);

        assertThat(underTest.convertEntity(acquisition))
            .returns(ACQUISITION_ID, Acquisition::getAcquisitionId)
            .returns(USER_ID, Acquisition::getUserId)
            .returns(ACQUIRED_AT, Acquisition::getAcquiredAt)
            .returns(STOCK_ITEM_ID, Acquisition::getStockItemId)
            .returns(AMOUNT, Acquisition::getAmount);
    }
}