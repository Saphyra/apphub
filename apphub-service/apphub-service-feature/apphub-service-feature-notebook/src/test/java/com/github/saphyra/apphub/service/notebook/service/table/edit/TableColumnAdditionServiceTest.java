package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
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
class TableColumnAdditionServiceTest {
	private static final UUID LIST_ITEM_ID = UUID.randomUUID();
	private static final Integer ROW_INDEX = 3;
	private static final Integer NEW_COLUMN_INDEX = 4;
	private static final Object DATA = "data";
	private static final String SERIALIZED_DATA = "serialized-data";
	private static final UUID STORED_FILE_ID = UUID.randomUUID();
	private static final UUID COLUMN_ID = UUID.randomUUID();

	@Mock
	private TableColumnFactory tableColumnFactory;

	@Mock
	private ColumnDataServiceProvider columnDataServiceProvider;

	@Mock
	private ContentFactory contentFactory;

	@InjectMocks
	private TableColumnAdditionService underTest;

	@Mock
	private ColumnDataService columnDataService;

	@Mock
	private TableColumn tableColumn;

	@Mock
	private Content content;

	@Test
	void processTableColumnAddition_noNewColumn() {
		List<TableColumn> columns = new ArrayList<>();
		List<Content> contents = new ArrayList<>();
		List<TableFileUploadResponse> fileUploads = new ArrayList<>();

		boolean result = underTest.processTableColumnAddition(
			LIST_ITEM_ID,
            ROW_INDEX,
			List.of(TableColumnModel.builder().itemType(ItemType.EXISTING).build()),
			columns,
			contents,
			fileUploads
		);

		assertThat(result).isFalse();
		assertThat(columns).isEmpty();
		assertThat(contents).isEmpty();
		assertThat(fileUploads).isEmpty();
		then(tableColumnFactory).shouldHaveNoInteractions();
	}

	@Test
	void processTableColumnAddition() {
		TableColumnModel model = TableColumnModel.builder()
			.itemType(ItemType.NEW)
			.columnIndex(NEW_COLUMN_INDEX)
			.columnType(ColumnType.FILE)
			.data(DATA)
			.build();
		List<TableColumn> columns = new ArrayList<>();
		List<Content> contents = new ArrayList<>();
		List<TableFileUploadResponse> fileUploads = new ArrayList<>();

		given(tableColumnFactory.create(NEW_COLUMN_INDEX, ColumnType.FILE)).willReturn(tableColumn);
		given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);
		given(columnDataService.serialize(DATA)).willReturn(Optional.of(new BiWrapper<>(SERIALIZED_DATA, Optional.of(STORED_FILE_ID))));
		given(tableColumn.getColumnId()).willReturn(COLUMN_ID);
		given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, SERIALIZED_DATA)).willReturn(content);

		boolean result = underTest.processTableColumnAddition(
			LIST_ITEM_ID,
            ROW_INDEX,
			List.of(TableColumnModel.builder().itemType(ItemType.EXISTING).build(), model),
			columns,
			contents,
			fileUploads
		);

		assertThat(result).isTrue();
		assertThat(columns).containsExactly(tableColumn);
		assertThat(contents).containsExactly(content);
		assertThat(fileUploads)
			.singleElement()
			.returns(NEW_COLUMN_INDEX, TableFileUploadResponse::getColumnIndex)
			.returns(STORED_FILE_ID, TableFileUploadResponse::getStoredFileId)
			.returns(ROW_INDEX, TableFileUploadResponse::getRowIndex);
	}

	@Test
	void createColumns() {
		TableColumnModel firstModel = TableColumnModel.builder()
			.columnIndex(NEW_COLUMN_INDEX)
			.columnType(ColumnType.FILE)
			.data(DATA)
			.build();
		TableColumnModel secondModel = TableColumnModel.builder()
			.columnIndex(NEW_COLUMN_INDEX + 1)
			.columnType(ColumnType.EMPTY)
			.build();
		List<TableFileUploadResponse> fileUploads = new ArrayList<>();
		List<Content> contents = new ArrayList<>();

		given(tableColumnFactory.create(NEW_COLUMN_INDEX, ColumnType.FILE)).willReturn(tableColumn);
		given(tableColumnFactory.create(NEW_COLUMN_INDEX + 1, ColumnType.EMPTY)).willReturn(tableColumn);
		given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);
		given(columnDataServiceProvider.getForType(ColumnType.EMPTY)).willReturn(columnDataService);
		given(columnDataService.serialize(DATA)).willReturn(Optional.of(new BiWrapper<>(SERIALIZED_DATA, Optional.of(STORED_FILE_ID))));
		given(columnDataService.serialize(null)).willReturn(Optional.empty());
		given(tableColumn.getColumnId()).willReturn(COLUMN_ID);
		given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, SERIALIZED_DATA)).willReturn(content);

		List<TableColumn> result = underTest.createColumns(
			LIST_ITEM_ID,
			List.of(firstModel, secondModel),
			ROW_INDEX,
			fileUploads,
			contents
		);

		assertThat(result).containsExactly(tableColumn, tableColumn);
		assertThat(contents).containsExactly(content);
		assertThat(fileUploads)
			.singleElement()
			.returns(ROW_INDEX, TableFileUploadResponse::getRowIndex)
			.returns(NEW_COLUMN_INDEX, TableFileUploadResponse::getColumnIndex)
			.returns(STORED_FILE_ID, TableFileUploadResponse::getStoredFileId);
	}
}