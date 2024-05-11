package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StockCategoryDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String STOCK_CATEGORY_ID_STRING = "stock-category-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StockCategoryConverter converter;

    @Mock
    private StockCategoryRepository repository;

    @InjectMocks
    private StockCategoryDao underTest;

    @Mock
    private StockCategoryEntity entity;

    @Mock
    private StockCategory domain;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(STOCK_CATEGORY_ID)).willReturn(STOCK_CATEGORY_ID_STRING);
        given(repository.findById(STOCK_CATEGORY_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(STOCK_CATEGORY_ID)).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(STOCK_CATEGORY_ID)).willReturn(STOCK_CATEGORY_ID_STRING);
        given(repository.findById(STOCK_CATEGORY_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(STOCK_CATEGORY_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByUserIdAndStockCategoryId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_CATEGORY_ID)).willReturn(STOCK_CATEGORY_ID_STRING);

        underTest.deleteByUserIdAndStockCategoryId(USER_ID, STOCK_CATEGORY_ID);

        then(repository).should().deleteByUserIdAndStockCategoryId(USER_ID_STRING, STOCK_CATEGORY_ID_STRING);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }
}