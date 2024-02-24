package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinMappingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final String PIN_GROUP_ID_STRING = "pin-group-id";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private PinMappingConverter converter;

    @Mock
    private PinMappingRepository repository;

    @InjectMocks
    private PinMappingDao underTest;

    @Mock
    private PinMapping domain;

    @Mock
    private PinMappingEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByPinGroupId() {
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(repository.getByPinGroupId(PIN_GROUP_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByPinGroupId(PIN_GROUP_ID)).containsExactly(domain);
    }

    @Test
    void deleteByPinGroupId() {
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);

        underTest.deleteByPinGroupId(PIN_GROUP_ID);

        then(repository).should().deleteByPinGroupId(PIN_GROUP_ID_STRING);
    }

    @Test
    void findByPinGroupIdAndListItemIdValidated_notFound() {
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findByPinGroupIdAndListItemId(PIN_GROUP_ID_STRING, LIST_ITEM_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByPinGroupIdAndListItemIdValidated(PIN_GROUP_ID, LIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByPinGroupIdAndListItemIdValidated() {
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findByPinGroupIdAndListItemId(PIN_GROUP_ID_STRING, LIST_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByPinGroupIdAndListItemIdValidated(PIN_GROUP_ID, LIST_ITEM_ID)).isEqualTo(domain);
    }

    @Test
    void deleteByListItemId() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.deleteByListItemId(LIST_ITEM_ID);

        then(repository).should().deleteByListItemId(LIST_ITEM_ID_STRING);
    }
}