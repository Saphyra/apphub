package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableHeadDaoTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";

    @Mock
    private TableHeadConverter converter;

    @Mock
    private TableHeadRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private TableHeadDao underTest;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableHeadEntity tableHeadEntity;

    @Test
    void save() {
        List<TableHead> tableHeads = List.of(tableHead);
        given(converter.convertDomain(LIST_ITEM_ID, tableHeads)).willReturn(tableHeadEntity);

        underTest.save(LIST_ITEM_ID, tableHeads);

        then(repository).should().save(tableHeadEntity);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.delete(LIST_ITEM_ID);

        then(repository).should().delete(LIST_ITEM_ID_STRING);
    }

    @Test
    void findByListItemIdValidated_notFound() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findByListItemId(LIST_ITEM_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByListItemIdValidated(LIST_ITEM_ID));
    }

    @Test
    void findByListItemIdValidated() {
        List<TableHead> tableHeads = List.of(tableHead);

        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.findByListItemId(LIST_ITEM_ID_STRING)).willReturn(Optional.of(tableHeadEntity));
        given(converter.convertEntity(tableHeadEntity)).willReturn(tableHeads);

        List<TableHead> result = underTest.findByListItemIdValidated(LIST_ITEM_ID);

        assertThat(result).isEqualTo(tableHeads);
    }
}

