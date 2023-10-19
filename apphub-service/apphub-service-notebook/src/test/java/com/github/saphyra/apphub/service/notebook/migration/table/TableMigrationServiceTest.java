package com.github.saphyra.apphub.service.notebook.migration.table;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableMigrationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 42;
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 456;
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private TableMigrationService underTest;

    @Mock
    private UnencryptedListItem table;

    @Mock
    private UnencryptedListItem other;

    @Mock
    private TableJoin tableJoin;

    @Mock
    private Dimension row;

    @Mock
    private Dimension column;

    @Mock
    private ColumnTypeDto columnType;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void migrateTable() {
        given(listItemDao.getAllUnencrypted()).willReturn(List.of(table, other));
        given(table.getType()).willReturn(ListItemType.TABLE);
        given(other.getType()).willReturn(ListItemType.CATEGORY);
        given(table.getUserId()).willReturn(USER_ID);
        given(table.getListItemId()).willReturn(LIST_ITEM_ID);

        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(tableJoin));
        given(tableJoin.getRowIndex()).willReturn(ROW_INDEX);
        given(tableJoin.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(tableJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);

        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, ROW_INDEX)).willReturn(row);
        given(row.getDimensionId()).willReturn(ROW_ID);

        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX, TABLE_JOIN_ID)).willReturn(column);
        given(columnTypeFactory.create(TABLE_JOIN_ID, USER_ID, ColumnType.TEXT)).willReturn(columnType);

        underTest.migrate();

        ArgumentCaptor<AccessTokenHeader> argumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(accessTokenProvider).should().set(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        then(dimensionDao).should().save(row);
        then(dimensionDao).should().save(column);
        then(columnTypeDao).should().save(columnType);

        then(accessTokenProvider).should().clear();

        then(checkedItemFactory).shouldHaveNoInteractions();
        then(checkedItemDao).shouldHaveNoInteractions();
    }

    @Test
    void migrateChecklistTable() {
        given(listItemDao.getAllUnencrypted()).willReturn(List.of(table, other));
        given(table.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(other.getType()).willReturn(ListItemType.CATEGORY);
        given(table.getUserId()).willReturn(USER_ID);
        given(table.getListItemId()).willReturn(LIST_ITEM_ID);

        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(tableJoin));
        given(tableJoin.getRowIndex()).willReturn(ROW_INDEX);
        given(tableJoin.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(tableJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);

        given(checklistTableRowDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(checklistTableRow));
        given(checklistTableRow.getRowIndex()).willReturn(ROW_INDEX);
        given(checklistTableRow.isChecked()).willReturn(true);

        given(checkedItemFactory.create(USER_ID, ROW_ID, true)).willReturn(checkedItem);
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, ROW_INDEX)).willReturn(row);
        given(row.getDimensionId()).willReturn(ROW_ID);

        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX, TABLE_JOIN_ID)).willReturn(column);
        given(columnTypeFactory.create(TABLE_JOIN_ID, USER_ID, ColumnType.TEXT)).willReturn(columnType);

        underTest.migrate();

        ArgumentCaptor<AccessTokenHeader> argumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(accessTokenProvider).should().set(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        then(dimensionDao).should().save(row);
        then(dimensionDao).should().save(column);
        then(columnTypeDao).should().save(columnType);
        then(checkedItemDao).should().save(checkedItem);

        then(accessTokenProvider).should().clear();
    }
}