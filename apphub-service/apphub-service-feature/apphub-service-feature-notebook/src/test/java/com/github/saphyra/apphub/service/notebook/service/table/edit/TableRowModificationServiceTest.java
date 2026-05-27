package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowModificationServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private TableColumnEditionService tableColumnEditionService;

    @InjectMocks
    private TableRowModificationService underTest;

    @Test
    void processTableRowModification_rowModified() {
        TableRow tableRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .index(1)
            .checked(false)
            .columns(new ArrayList<>(List.of(TableColumn.builder().columnId(UUID.randomUUID()).type(ColumnType.DATE).build())))
            .build();
        TableRowModel model = TableRowModel.builder()
            .rowId(ROW_ID)
            .rowIndex(2)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .columns(List.of())
            .build();

        given(tableColumnEditionService.processTableColumnEdition(LIST_ITEM_ID, List.of(), tableRow.getColumns(), List.of(), List.of(), 2)).willReturn(false);

        underTest.processTableRowModification(LIST_ITEM_ID, List.of(model), List.of(tableRow), List.of(), List.of());

        assertThat(tableRow.getChecked()).isTrue();
        assertThat(tableRow.getIndex()).isEqualTo(2);
        then(tableRowDao).should().save(tableRow);
    }

    @Test
    void processTableRowModification_notModified() {
        TableRow tableRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .index(1)
            .checked(false)
            .columns(new ArrayList<>(List.of(TableColumn.builder().columnId(UUID.randomUUID()).type(ColumnType.DATE).build())))
            .build();
        TableRowModel model = TableRowModel.builder()
            .rowId(ROW_ID)
            .rowIndex(1)
            .checked(false)
            .itemType(ItemType.EXISTING)
            .columns(List.of())
            .build();

        given(tableColumnEditionService.processTableColumnEdition(LIST_ITEM_ID, List.of(), tableRow.getColumns(), List.of(), List.of(), 1)).willReturn(false);

        underTest.processTableRowModification(LIST_ITEM_ID, List.of(model), List.of(tableRow), List.of(), List.of());

        then(tableRowDao).shouldHaveNoInteractions();
    }
}

