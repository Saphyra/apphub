package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AcquisitionDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final LocalDate ACQUIRED_AT = LocalDate.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AcquisitionConverter converter;

    @Mock
    private AcquisitionRepository repository;

    @InjectMocks
    private AcquisitionDao underTest;

    @Mock
    private AcquisitionEntity entity;

    @Mock
    private Acquisition domain;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getDistinctAcquiredAtByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getDistinctAcquiredAtByUserId(USER_ID_STRING)).willReturn(List.of(ACQUIRED_AT.toString()));

        assertThat(underTest.getDistinctAcquiredAtByUserId(USER_ID)).containsExactly(ACQUIRED_AT);
    }

    @Test
    void getByAcquiredAtAndUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByAcquiredAtAndUserId(ACQUIRED_AT.toString(), USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByAcquiredAtAndUserId(ACQUIRED_AT, USER_ID)).containsExactly(domain);
    }

    @Test
    void deleteByStockItemIdAndUserId() {
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByStockItemIdAndUserId(STOCK_ITEM_ID, USER_ID);

        then(repository).should().deleteByStockItemIdAndUserId(STOCK_ITEM_ID_STRING, USER_ID_STRING);
    }
}