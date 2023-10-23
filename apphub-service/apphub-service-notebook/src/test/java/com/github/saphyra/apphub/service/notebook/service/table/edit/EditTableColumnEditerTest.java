package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableColumnDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableColumnEditerTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableColumnDeletionService tableColumnDeletionService;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @InjectMocks
    private EditTableColumnEditer underTest;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private ListItem listItem;

    @Mock
    private TableColumnModel model;

    @Mock
    private ColumnDataService columnDataService;

    @Mock
    private ColumnTypeDto columnType;

    @Test
    void existingColumn() {
        given(model.getItemType()).willReturn(ItemType.EXISTING);
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnType()).willReturn(ColumnType.TEXT);
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnType);
        given(columnType.getType()).willReturn(ColumnType.TEXT);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.TEXT)).willReturn(columnDataService);
        given(columnDataService.edit(listItem, ROW_ID, model)).willReturn(Optional.of(fileUploadResponse));

        assertThat(underTest.editTableColumn(listItem, ROW_ID, model)).containsExactly(fileUploadResponse);

        then(tableColumnDeletionService).shouldHaveNoInteractions();
    }

    @Test
    void existingColumnDifferentColumnType() {
        given(model.getItemType()).willReturn(ItemType.EXISTING);
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnType()).willReturn(ColumnType.LINK);
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnType);
        given(columnType.getType()).willReturn(ColumnType.TEXT);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.LINK)).willReturn(columnDataService);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(columnDataService.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).willReturn(Optional.of(fileUploadResponse));

        assertThat(underTest.editTableColumn(listItem, ROW_ID, model)).containsExactly(fileUploadResponse);

        then(tableColumnDeletionService).should().deleteColumn(COLUMN_ID);
    }

    @Test
    void newColumn() {
        given(model.getItemType()).willReturn(ItemType.NEW);
        given(model.getColumnType()).willReturn(ColumnType.LINK);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.LINK)).willReturn(columnDataService);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(columnDataService.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).willReturn(Optional.of(fileUploadResponse));

        assertThat(underTest.editTableColumn(listItem, ROW_ID, model)).containsExactly(fileUploadResponse);

        then(tableColumnDeletionService).shouldHaveNoInteractions();
    }
}