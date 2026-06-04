package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContent;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.DeprecatedTableHead;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.DeprecatedTableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListItemMigratorUtilTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String DATA = "data";
    private static final String CONTENT = "content";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final String TABLE_COLUMN_CONTENT = "table-column-content";

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private DeprecatedContentDao contentDao;

    @Mock
    private DeprecatedTableHeadDao deprecatedTableHeadDao;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ListItemMigratorUtil underTest;

    @Test
    void migrate_withoutData() {
        DeprecatedListItem original = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE)
            .pinned(true)
            .archived(false)
            .build();

        ListItem result = underTest.migrate(original);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT_ID);
        assertThat(result.getType()).isEqualTo(ListItemType.CATEGORY);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.isPinned()).isTrue();
        assertThat(result.isArchived()).isFalse();
        assertThat(result.getData()).isNull();
    }

    @Test
    void migrate_withData() {
        DeprecatedListItem original = DeprecatedListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT_ID)
            .type(ListItemType.TEXT)
            .title(TITLE)
            .pinned(false)
            .archived(true)
            .build();

        ListItem result = underTest.migrate(original, DATA);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT_ID);
        assertThat(result.getType()).isEqualTo(ListItemType.TEXT);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.isPinned()).isFalse();
        assertThat(result.isArchived()).isTrue();
        assertThat(result.getData()).isEqualTo(DATA);
    }

    @Test
    void migrateChecklist() {
        UUID dimensionId = UUID.randomUUID();
        int dimensionIndex = 0;

        Dimension dimension = Dimension.builder()
            .dimensionId(dimensionId)
            .userId(USER_ID)
            .externalReference(LIST_ITEM_ID)
            .index(dimensionIndex)
            .build();

        CheckedItem checkedItem = CheckedItem.builder()
            .checkedItemId(dimensionId)
            .userId(USER_ID)
            .checked(true)
            .build();

        DeprecatedContent deprecatedContent = DeprecatedContent.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(dimensionId)
            .content(CONTENT)
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(TITLE)
            .build();

        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(dimension));
        given(checkedItemDao.findByIdValidated(dimensionId)).willReturn(checkedItem);
        given(contentDao.findByParentValidated(dimensionId)).willReturn(deprecatedContent);

        List<BiWrapper<ChecklistItem, Content>> result = underTest.migrateChecklist(listItem);

        assertThat(result).hasSize(1);
        ChecklistItem checklistItem = result.getFirst().getEntity1();
        assertThat(checklistItem.getChecklistItemId()).isEqualTo(dimensionId);
        assertThat(checklistItem.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(checklistItem.isChecked()).isTrue();
        assertThat(checklistItem.getIndex()).isEqualTo(dimensionIndex);

        Content content = result.getFirst().getEntity2();
        assertThat(content.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(content.get(dimensionId)).isEqualTo(CONTENT);
    }

    @Test
    void migrateTable_withEmptyColumn() {
        UUID tableHeadId = UUID.randomUUID();
        UUID rowId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        DeprecatedTableHead deprecatedTableHead = DeprecatedTableHead.builder()
            .tableHeadId(tableHeadId)
            .userId(USER_ID)
            .parent(LIST_ITEM_ID)
            .columnIndex(0)
            .build();

        DeprecatedContent headContent = DeprecatedContent.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(tableHeadId)
            .content(TABLE_HEAD_CONTENT)
            .build();

        Dimension rowDimension = Dimension.builder()
            .dimensionId(rowId)
            .userId(USER_ID)
            .externalReference(LIST_ITEM_ID)
            .index(0)
            .build();

        Dimension columnDimension = Dimension.builder()
            .dimensionId(columnId)
            .userId(USER_ID)
            .externalReference(rowId)
            .index(0)
            .build();

        ColumnTypeDto columnTypeDto = ColumnTypeDto.builder()
            .columnId(columnId)
            .userId(USER_ID)
            .type(ColumnType.EMPTY)
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title(TITLE)
            .build();

        given(deprecatedTableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(deprecatedTableHead));
        given(contentDao.findByParentValidated(tableHeadId)).willReturn(headContent);
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(rowDimension));
        given(checkedItemDao.findById(rowId)).willReturn(Optional.empty());
        given(dimensionDao.getByExternalReference(rowId)).willReturn(List.of(columnDimension));
        given(columnTypeDao.findByIdValidated(columnId)).willReturn(columnTypeDto);

        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> result = underTest.migrateTable(listItem);

        List<TableHead> tableHeads = result.getEntity1();
        assertThat(tableHeads).hasSize(1);
        assertThat(tableHeads.getFirst().getTableHeadId()).isEqualTo(tableHeadId);
        assertThat(tableHeads.getFirst().getIndex()).isEqualTo(0);

        List<TableRow> tableRows = result.getEntity2();
        assertThat(tableRows).hasSize(1);
        TableRow tableRow = tableRows.getFirst();
        assertThat(tableRow.getTableRowId()).isEqualTo(rowId);
        assertThat(tableRow.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(tableRow.getIndex()).isEqualTo(0);
        assertThat(tableRow.getChecked()).isNull();

        List<TableColumn> columns = tableRow.getColumns();
        assertThat(columns).hasSize(1);
        assertThat(columns.getFirst().getColumnId()).isEqualTo(columnId);
        assertThat(columns.getFirst().getType()).isEqualTo(ColumnType.EMPTY);

        // No content added for EMPTY columns (only head content)
        List<Content> contents = result.getEntity3();
        assertThat(contents).hasSize(1);
        assertThat(contents.getFirst().get(tableHeadId)).isEqualTo(TABLE_HEAD_CONTENT);
    }

    @Test
    void migrateTable_withTextColumn() {
        UUID tableHeadId = UUID.randomUUID();
        UUID rowId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        DeprecatedTableHead deprecatedTableHead = DeprecatedTableHead.builder()
            .tableHeadId(tableHeadId)
            .userId(USER_ID)
            .parent(LIST_ITEM_ID)
            .columnIndex(0)
            .build();

        DeprecatedContent headContent = DeprecatedContent.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(tableHeadId)
            .content(TABLE_HEAD_CONTENT)
            .build();

        Dimension rowDimension = Dimension.builder()
            .dimensionId(rowId)
            .userId(USER_ID)
            .externalReference(LIST_ITEM_ID)
            .index(0)
            .build();

        Dimension columnDimension = Dimension.builder()
            .dimensionId(columnId)
            .userId(USER_ID)
            .externalReference(rowId)
            .index(0)
            .build();

        ColumnTypeDto columnTypeDto = ColumnTypeDto.builder()
            .columnId(columnId)
            .userId(USER_ID)
            .type(ColumnType.TEXT)
            .build();

        DeprecatedContent columnContent = DeprecatedContent.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(columnId)
            .content(TABLE_COLUMN_CONTENT)
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title(TITLE)
            .build();

        given(deprecatedTableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(deprecatedTableHead));
        given(contentDao.findByParentValidated(tableHeadId)).willReturn(headContent);
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(rowDimension));
        given(checkedItemDao.findById(rowId)).willReturn(Optional.empty());
        given(dimensionDao.getByExternalReference(rowId)).willReturn(List.of(columnDimension));
        given(columnTypeDao.findByIdValidated(columnId)).willReturn(columnTypeDto);
        given(contentDao.findByParentValidated(columnId)).willReturn(columnContent);

        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> result = underTest.migrateTable(listItem);

        List<Content> contents = result.getEntity3();
        assertThat(contents).hasSize(2);
        assertThat(contents.get(1).get(columnId)).isEqualTo(TABLE_COLUMN_CONTENT);
    }

    @Test
    void migrateTable_withFileColumn() {
        UUID tableHeadId = UUID.randomUUID();
        UUID rowId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID storedFileId = UUID.randomUUID();
        String fileMetadataJson = "{\"storedFileId\":\"" + storedFileId + "\"}";

        DeprecatedTableHead deprecatedTableHead = DeprecatedTableHead.builder()
            .tableHeadId(tableHeadId)
            .userId(USER_ID)
            .parent(LIST_ITEM_ID)
            .columnIndex(0)
            .build();

        DeprecatedContent headContent = DeprecatedContent.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(tableHeadId)
            .content(TABLE_HEAD_CONTENT)
            .build();

        Dimension rowDimension = Dimension.builder()
            .dimensionId(rowId)
            .userId(USER_ID)
            .externalReference(LIST_ITEM_ID)
            .index(0)
            .build();

        Dimension columnDimension = Dimension.builder()
            .dimensionId(columnId)
            .userId(USER_ID)
            .externalReference(rowId)
            .index(0)
            .build();

        ColumnTypeDto columnTypeDto = ColumnTypeDto.builder()
            .columnId(columnId)
            .userId(USER_ID)
            .type(ColumnType.FILE)
            .build();

        File file = File.builder()
            .fileId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(columnId)
            .storedFileId(storedFileId)
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title(TITLE)
            .build();

        given(deprecatedTableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(deprecatedTableHead));
        given(contentDao.findByParentValidated(tableHeadId)).willReturn(headContent);
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(rowDimension));
        given(checkedItemDao.findById(rowId)).willReturn(Optional.empty());
        given(dimensionDao.getByExternalReference(rowId)).willReturn(List.of(columnDimension));
        given(columnTypeDao.findByIdValidated(columnId)).willReturn(columnTypeDto);
        given(fileDao.findByParentValidated(columnId)).willReturn(file);
        given(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).willReturn(fileMetadataJson);

        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> result = underTest.migrateTable(listItem);

        List<Content> contents = result.getEntity3();
        assertThat(contents).hasSize(2);
        assertThat(contents.get(1).get(columnId)).isEqualTo(fileMetadataJson);
    }

    @Test
    void migrateTable_withCheckedRow() {
        UUID rowId = UUID.randomUUID();

        Dimension rowDimension = Dimension.builder()
            .dimensionId(rowId)
            .userId(USER_ID)
            .externalReference(LIST_ITEM_ID)
            .index(0)
            .build();

        CheckedItem checkedItem = CheckedItem.builder()
            .checkedItemId(rowId)
            .userId(USER_ID)
            .checked(true)
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST_TABLE)
            .title(TITLE)
            .build();

        given(deprecatedTableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of());
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(rowDimension));
        given(checkedItemDao.findById(rowId)).willReturn(Optional.of(checkedItem));
        given(dimensionDao.getByExternalReference(rowId)).willReturn(List.of());

        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> result = underTest.migrateTable(listItem);

        List<TableRow> tableRows = result.getEntity2();
        assertThat(tableRows).hasSize(1);
        assertThat(tableRows.getFirst().getChecked()).isTrue();
    }
}


