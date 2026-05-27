package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class TableRowEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableRowModificationService tableRowModificationService;

    @Mock
    private TableRowDeletionService tableRowDeletionService;

    @Mock
    private TableRowAdditionService tableRowAdditionService;

    @InjectMocks
    private TableRowEditionService underTest;

    @Test
    void processTableRows() {
        TableFileUploadResponse upload = TableFileUploadResponse.builder().rowIndex(1).columnIndex(2).build();
        List<TableRow> tableRows = new ArrayList<>();
        List<Content> contents = new ArrayList<>();

        doAnswer(invocation -> {
            List<TableFileUploadResponse> fileUploads = invocation.getArgument(3);
            fileUploads.add(upload);
            return null;
        }).when(tableRowAdditionService).processTableRowAddition(eq(LIST_ITEM_ID), eq(List.of()), eq(tableRows), anyList(), eq(contents));

        List<TableFileUploadResponse> result = underTest.processTableRows(LIST_ITEM_ID, List.of(), tableRows, contents);

        assertThat(result).containsExactly(upload);
        then(tableRowDeletionService).should().processTableRowDeletion(LIST_ITEM_ID, List.of(), tableRows, contents);
        then(tableRowAdditionService).should().processTableRowAddition(LIST_ITEM_ID, List.of(), tableRows, result, contents);
        then(tableRowModificationService).should().processTableRowModification(LIST_ITEM_ID, List.of(), tableRows, result, contents);
    }
}

