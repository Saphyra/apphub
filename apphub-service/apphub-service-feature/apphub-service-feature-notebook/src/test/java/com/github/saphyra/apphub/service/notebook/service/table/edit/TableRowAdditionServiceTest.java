package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
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
class TableRowAdditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableRowFactory tableRowFactory;

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private TableColumnAdditionService tableColumnAdditionService;

    @InjectMocks
    private TableRowAdditionService underTest;

    @Test
    void processTableRowAddition() {
        TableRowModel newModel = TableRowModel.builder()
            .itemType(ItemType.NEW)
            .rowIndex(2)
            .checked(true)
            .columns(List.of())
            .build();
        TableRowModel existingModel = TableRowModel.builder()
            .itemType(ItemType.EXISTING)
            .build();
        TableRow tableRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(UUID.randomUUID())
            .index(2)
            .checked(true)
            .columns(List.of())
            .build();
        List<TableColumn> columns = List.of(TableColumn.builder().columnId(UUID.randomUUID()).type(ColumnType.DATE).build());
        List<TableRow> tableRows = new ArrayList<>();

        given(tableColumnAdditionService.createColumns(LIST_ITEM_ID, newModel.getColumns(), 2, List.of(), List.of())).willReturn(columns);
        given(tableRowFactory.create(LIST_ITEM_ID, 2, true, columns)).willReturn(tableRow);

        underTest.processTableRowAddition(LIST_ITEM_ID, List.of(newModel, existingModel), tableRows, List.of(), List.of());

        assertThat(tableRows).containsExactly(tableRow);
        then(tableRowDao).should().save(tableRow);
    }
}

