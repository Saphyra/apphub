package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
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
class ListItemDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String PARENT_STRING = "parent";

    @Mock
    private ListItemConverter converter;

    @Mock
    private ListItemRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ListItemDao underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ListItemEntity entity;

    @Test
    void save() {
        given(converter.convertDomain(listItem)).willReturn(entity);

        underTest.save(listItem);

        then(repository).should().save(entity);
    }

    @Test
    void getByUserIdAndParent() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByUserIdAndParent(USER_ID_STRING, PARENT_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(listItem));

        List<ListItem> result = underTest.getByUserIdAndParent(USER_ID, PARENT);

        assertThat(result).containsExactly(listItem);
    }

    @Test
    void getByUserIdAndType() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserIdAndType(USER_ID_STRING, ListItemType.TEXT.name())).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(listItem));

        List<ListItem> result = underTest.getByUserIdAndType(USER_ID, ListItemType.TEXT);

        assertThat(result).containsExactly(listItem);
    }

    @Test
    void findByIdValidated_found() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findById(USER_ID_STRING, LIST_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(listItem));

        ListItem result = underTest.findByIdValidated(USER_ID, LIST_ITEM_ID);

        assertThat(result).isEqualTo(listItem);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findById(USER_ID_STRING, LIST_ITEM_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(USER_ID, LIST_ITEM_ID), HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(listItem));

        List<ListItem> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(listItem);
    }

    @Test
    void delete() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.delete(listItem);

        then(repository).should().delete(USER_ID_STRING, LIST_ITEM_ID_STRING);
    }
}