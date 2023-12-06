package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;

import java.util.List;
import java.util.UUID;

class CustomTableUtils {
    public static CreateTableRequest createCustomTableRequest(String title, String tableHeadContent, ColumnType columnType, Object data){
        return CreateTableRequest.builder()
            .title(title)
            .listItemType(ListItemType.CUSTOM_TABLE)
            .tableHeads(List.of(
                TableHeadModel.builder()
                    .columnIndex(0)
                    .content(tableHeadContent)
                    .build()
            ))
            .rows(List.of(
                TableRowModel.builder()
                    .rowIndex(0)
                    .columns(List.of(
                        TableColumnModel.builder()
                            .columnIndex(0)
                            .columnType(columnType)
                            .data(data)
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    public static EditTableRequest createEditCustomTableRequest(String title, UUID tableHeadId, String tableHeadContent, UUID rowId, UUID columnId, ColumnType columnType, Object data){
        return EditTableRequest.builder()
            .title(title)
            .tableHeads(List.of(
                TableHeadModel.builder()
                    .tableHeadId(tableHeadId)
                    .columnIndex(0)
                    .content(tableHeadContent)
                    .type(ItemType.EXISTING)
                    .build()
            ))
            .rows(List.of(
                TableRowModel.builder()
                    .rowId(rowId)
                    .rowIndex(0)
                    .itemType(ItemType.EXISTING)
                    .columns(List.of(
                        TableColumnModel.builder()
                            .columnId(columnId)
                            .columnIndex(0)
                            .columnType(columnType)
                            .itemType(ItemType.EXISTING)
                            .data(data)
                            .build()
                    ))
                    .build()
            ))
            .build();
    }
}
