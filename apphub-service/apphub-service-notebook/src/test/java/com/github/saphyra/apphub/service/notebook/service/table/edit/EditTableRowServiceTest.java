package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableRowServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTableRowDeleter editTableRowDeleter;

    @Mock
    private EditTableRowEditer editTableRowEditer;

    @InjectMocks
    private EditTableRowService underTest;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private ListItem listItem;

    @Mock
    private TableRowModel rowModel;

    @Test
    void editTableRows() {
        given(editTableRowEditer.updateRows(listItem, List.of(rowModel))).willReturn(List.of(fileUploadResponse));
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        assertThat(underTest.editTableRows(listItem, List.of(rowModel))).containsExactly(fileUploadResponse);

        then(editTableRowDeleter).should().deleteRows(LIST_ITEM_ID, List.of(rowModel));
    }
}