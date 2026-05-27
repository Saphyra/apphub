package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableColumnModificationServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final int ROW_INDEX = 5;
    private static final int COLUMN_INDEX = 2;
    private static final int NEW_COLUMN_INDEX = 7;
    private static final String EXISTING_DATA = "existing-data";
    private static final String NEW_DATA = "new-data";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private TableColumnModificationService underTest;

    @Test
    void newColumn() {
        TableColumnModel model = TableColumnModel.builder()
            .itemType(ItemType.NEW)
            .build();

        boolean result = underTest.processTableColumnModification(LIST_ITEM_ID, ROW_INDEX, List.of(model), List.of(), List.of(), new ArrayList<>());

        assertThat(result).isFalse();
        then(columnDataServiceProvider).shouldHaveNoInteractions();
        then(contentFactory).shouldHaveNoInteractions();
    }

    @Test
    void columnUnchanged() {
        TableColumnModel model = TableColumnModel.builder()
            .itemType(ItemType.EXISTING)
            .columnId(COLUMN_ID)
            .columnIndex(COLUMN_INDEX)
            .columnType(ColumnType.TEXT)
            .data(EXISTING_DATA)
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(COLUMN_INDEX)
            .type(ColumnType.TEXT)
            .build();

        Content existingContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, EXISTING_DATA);
        BiWrapper<String, Optional<UUID>> serialized = new BiWrapper<>(EXISTING_DATA, Optional.empty());

        given(columnDataServiceProvider.getForType(ColumnType.TEXT)).willReturn(columnDataService);
        given(columnDataService.serialize(EXISTING_DATA)).willReturn(Optional.of(serialized));

        List<Content> contents = new ArrayList<>(List.of(existingContent));
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();

        boolean result = underTest.processTableColumnModification(LIST_ITEM_ID, ROW_INDEX, List.of(model), List.of(column), contents, fileUploads);

        assertThat(result).isTrue();
        assertThat(contents).containsExactly(existingContent);
        assertThat(existingContent.get(COLUMN_ID)).isEqualTo(EXISTING_DATA);
        assertThat(fileUploads).isEmpty();
        then(contentFactory).shouldHaveNoInteractions();
    }

    @Test
    void onlyColumnIndexChanged() {
        TableColumnModel model = TableColumnModel.builder()
            .itemType(ItemType.EXISTING)
            .columnId(COLUMN_ID)
            .columnIndex(NEW_COLUMN_INDEX)
            .columnType(ColumnType.EMPTY)
            .data(null)
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(COLUMN_INDEX)
            .type(ColumnType.EMPTY)
            .build();

        given(columnDataServiceProvider.getForType(ColumnType.EMPTY)).willReturn(columnDataService);
        given(columnDataService.serialize(null)).willReturn(Optional.empty());

        boolean result = underTest.processTableColumnModification(LIST_ITEM_ID, ROW_INDEX, List.of(model), List.of(column), List.of(), new ArrayList<>());

        assertThat(result).isTrue();
        assertThat(column.getIndex()).isEqualTo(NEW_COLUMN_INDEX);
        then(contentFactory).shouldHaveNoInteractions();
    }

    @Test
    void unchangedColumnTypeWithDifferentData() {
        TableColumnModel model = TableColumnModel.builder()
            .itemType(ItemType.EXISTING)
            .columnId(COLUMN_ID)
            .columnIndex(COLUMN_INDEX)
            .columnType(ColumnType.FILE)
            .data(NEW_DATA)
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(COLUMN_INDEX)
            .type(ColumnType.FILE)
            .build();

        Content existingContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, EXISTING_DATA);

        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, NEW_DATA);

        BiWrapper<String, Optional<UUID>> serialized = new BiWrapper<>(NEW_DATA, Optional.of(STORED_FILE_ID));
        TableFileUploadResponse expectedFileUpload = TableFileUploadResponse.builder()
            .rowIndex(ROW_INDEX)
            .columnIndex(COLUMN_INDEX)
            .storedFileId(STORED_FILE_ID)
            .build();

        given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);
        given(columnDataService.serialize(NEW_DATA)).willReturn(Optional.of(serialized));
        given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, NEW_DATA)).willReturn(newContent);

        List<Content> contents = new ArrayList<>(List.of(existingContent));
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();

        boolean result = underTest.processTableColumnModification(LIST_ITEM_ID, ROW_INDEX, List.of(model), List.of(column), contents, fileUploads);

        assertThat(result).isTrue();
        assertThat(contents).contains(newContent);
        assertThat(existingContent.contains(COLUMN_ID)).isFalse();
        assertThat(fileUploads).containsExactly(expectedFileUpload);
        then(columnDataService).should().deleteData(EXISTING_DATA);
    }

    @Test
    void columnTypeChanged() {
        TableColumnModel model = TableColumnModel.builder()
            .itemType(ItemType.EXISTING)
            .columnId(COLUMN_ID)
            .columnIndex(COLUMN_INDEX)
            .columnType(ColumnType.FILE)
            .data(NEW_DATA)
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(COLUMN_INDEX)
            .type(ColumnType.TEXT)
            .build();

        Content existingContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, EXISTING_DATA);

        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, NEW_DATA);

        BiWrapper<String, Optional<UUID>> serialized = new BiWrapper<>(NEW_DATA, Optional.of(STORED_FILE_ID));
        TableFileUploadResponse expectedFileUpload = TableFileUploadResponse.builder()
            .rowIndex(ROW_INDEX)
            .columnIndex(COLUMN_INDEX)
            .storedFileId(STORED_FILE_ID)
            .build();

        given(columnDataServiceProvider.getForType(ColumnType.TEXT)).willReturn(columnDataService);
        given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);
        given(columnDataService.serialize(NEW_DATA)).willReturn(Optional.of(serialized));
        given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, NEW_DATA)).willReturn(newContent);

        List<Content> contents = new ArrayList<>(List.of(existingContent));
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();

        boolean result = underTest.processTableColumnModification(LIST_ITEM_ID, ROW_INDEX, List.of(model), List.of(column), contents, fileUploads);

        assertThat(result).isTrue();
        assertThat(column.getType()).isEqualTo(ColumnType.FILE);
        assertThat(contents).contains(newContent);
        assertThat(existingContent.contains(COLUMN_ID)).isFalse();
        assertThat(fileUploads).containsExactly(expectedFileUpload);
        then(columnDataService).should().deleteData(EXISTING_DATA);
    }
}
