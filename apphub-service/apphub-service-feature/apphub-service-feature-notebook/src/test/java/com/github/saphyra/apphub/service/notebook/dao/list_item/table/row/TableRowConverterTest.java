package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowConverter.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowConverter.COLUMN_COLUMNS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowConverter.COLUMN_INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableRowConverterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_ROW_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final String TABLE_ROW_ID_STRING = "table-row-id";
    private static final String USER_ID = "user-id";
    private static final int INDEX = 3;
    private static final String INDEX_ENCRYPTED = "index-encrypted";
    private static final Boolean CHECKED = true;
    private static final String CHECKED_ENCRYPTED = "checked-encrypted";
    private static final String COLUMNS_SERIALIZED = "[{...}]";
    private static final String COLUMNS_ENCRYPTED = "columns-encrypted";

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TableRowConverter underTest;

    @Mock
    private TableColumn tableColumn;

    @Test
    void convertDomain() {
        List<TableColumn> columns = List.of(tableColumn);
        TableRow domain = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(TABLE_ROW_ID)
            .index(INDEX)
            .checked(CHECKED)
            .columns(columns)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(uuidConverter.convertDomain(TABLE_ROW_ID)).willReturn(TABLE_ROW_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(integerEncryptor.encrypt(INDEX, USER_ID, TABLE_ROW_ID_STRING, COLUMN_INDEX)).willReturn(INDEX_ENCRYPTED);
        given(booleanEncryptor.encrypt(CHECKED, USER_ID, TABLE_ROW_ID_STRING, COLUMN_CHECKED)).willReturn(CHECKED_ENCRYPTED);
        given(objectMapper.writeValueAsString(columns)).willReturn(COLUMNS_SERIALIZED);
        given(stringEncryptor.encrypt(COLUMNS_SERIALIZED, USER_ID, TABLE_ROW_ID_STRING, COLUMN_COLUMNS)).willReturn(COLUMNS_ENCRYPTED);

        TableRowEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(TABLE_ROW_ID_STRING, TableRowEntity::getTableRowId)
            .returns(LIST_ITEM_ID_STRING, TableRowEntity::getListItemId)
            .returns(INDEX_ENCRYPTED, TableRowEntity::getIndex)
            .returns(CHECKED_ENCRYPTED, TableRowEntity::getChecked)
            .returns(COLUMNS_ENCRYPTED, TableRowEntity::getColumns);
    }

    @Test
    void convertEntity() {
        List<TableColumn> columns = List.of(tableColumn);
        TableRowEntity entity = TableRowEntity.builder()
            .listItemId(LIST_ITEM_ID_STRING)
            .tableRowId(TABLE_ROW_ID_STRING)
            .index(INDEX_ENCRYPTED)
            .checked(CHECKED_ENCRYPTED)
            .columns(COLUMNS_ENCRYPTED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID);
        given(uuidConverter.convertEntity(TABLE_ROW_ID_STRING)).willReturn(TABLE_ROW_ID);
        given(uuidConverter.convertEntity(LIST_ITEM_ID_STRING)).willReturn(LIST_ITEM_ID);
        given(integerEncryptor.decrypt(INDEX_ENCRYPTED, USER_ID, TABLE_ROW_ID_STRING, COLUMN_INDEX)).willReturn(INDEX);
        given(booleanEncryptor.decrypt(CHECKED_ENCRYPTED, USER_ID, TABLE_ROW_ID_STRING, COLUMN_CHECKED)).willReturn(CHECKED);
        given(stringEncryptor.decrypt(COLUMNS_ENCRYPTED, USER_ID, TABLE_ROW_ID_STRING, COLUMN_COLUMNS)).willReturn(COLUMNS_SERIALIZED);
        given(objectMapper.readValue(eq(COLUMNS_SERIALIZED), any(TypeReference.class))).willReturn(columns);

        TableRow result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(TABLE_ROW_ID, TableRow::getTableRowId)
            .returns(LIST_ITEM_ID, TableRow::getListItemId)
            .returns(INDEX, TableRow::getIndex)
            .returns(CHECKED, TableRow::getChecked)
            .returns(columns, TableRow::getColumns);
    }
}

