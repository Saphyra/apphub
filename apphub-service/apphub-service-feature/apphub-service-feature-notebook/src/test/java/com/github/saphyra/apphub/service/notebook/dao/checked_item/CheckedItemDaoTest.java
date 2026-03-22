package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CheckedItemDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID CHECKED_ITEM_ID = UUID.randomUUID();
    private static final String CHECKED_ITEM_ID_STRING = "checked-item-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CheckedItemConverter converter;

    @Mock
    private CheckedItemRepository repository;

    @InjectMocks
    private CheckedItemDao underTest;

    @Mock
    private CheckedItem domain;

    @Mock
    private CheckedItemEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void findByIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(CHECKED_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(CHECKED_ITEM_ID)).willReturn(CHECKED_ITEM_ID_STRING);
        given(repository.findById(CHECKED_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        CheckedItem result = underTest.findByIdValidated(CHECKED_ITEM_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(CHECKED_ITEM_ID)).willReturn(CHECKED_ITEM_ID_STRING);
        given(repository.existsById(CHECKED_ITEM_ID_STRING)).willReturn(true);

        underTest.deleteById(CHECKED_ITEM_ID);

        then(repository).should().deleteById(CHECKED_ITEM_ID_STRING);
    }
}