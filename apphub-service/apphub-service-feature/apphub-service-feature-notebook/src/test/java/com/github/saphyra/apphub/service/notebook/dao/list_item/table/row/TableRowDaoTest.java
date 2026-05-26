package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

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
class TableRowDaoTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_ROW_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String TABLE_ROW_ID_STRING = "table-row-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private TableRowRepository repository;

    @Mock
    private TableRowConverter converter;

    @InjectMocks
    private TableRowDao underTest;

    @Mock
    private TableRow domain;

    @Mock
    private TableRowEntity entity;

    @Test
    void delete_single() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(TABLE_ROW_ID)).willReturn(TABLE_ROW_ID_STRING);

        underTest.delete(LIST_ITEM_ID, TABLE_ROW_ID);

        then(repository).should().delete(LIST_ITEM_ID_STRING, TABLE_ROW_ID_STRING);
    }

    @Test
    void save_single() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
    }

    @Test
    void delete_list() {
        given(domain.getTableRowId()).willReturn(TABLE_ROW_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(TABLE_ROW_ID)).willReturn(TABLE_ROW_ID_STRING);

        underTest.delete(LIST_ITEM_ID, List.of(domain));

        then(repository).should().delete(LIST_ITEM_ID_STRING, List.of(TABLE_ROW_ID_STRING));
    }

    @Test
    void save_list() {
        List<TableRow> rows = List.of(domain);
        given(converter.convertDomain(rows)).willReturn(List.of(entity));

        underTest.save(rows);

        then(repository).should().save(List.of(entity));
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(TABLE_ROW_ID)).willReturn(TABLE_ROW_ID_STRING);
        given(repository.findById(LIST_ITEM_ID_STRING, TABLE_ROW_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(LIST_ITEM_ID, TABLE_ROW_ID));
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(TABLE_ROW_ID)).willReturn(TABLE_ROW_ID_STRING);
        given(repository.findById(LIST_ITEM_ID_STRING, TABLE_ROW_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        TableRow result = underTest.findByIdValidated(LIST_ITEM_ID, TABLE_ROW_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void getByListItemId() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<TableRow> result = underTest.getByListItemId(LIST_ITEM_ID);

        assertThat(result).containsExactly(domain);
    }
}

