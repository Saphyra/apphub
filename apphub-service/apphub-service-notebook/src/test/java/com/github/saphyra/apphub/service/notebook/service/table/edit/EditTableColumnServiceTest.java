package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
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
class EditTableColumnServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private EditTableColumnDeleter editTableColumnDeleter;

    @Mock
    private EditTableColumnEditer editTableColumnEditer;

    @InjectMocks
    private EditTableColumnService underTest;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private ListItem listItem;

    @Mock
    private TableColumnModel model;

    @Test
    void editTableColumns() {
        given(editTableColumnEditer.editTableColumn(listItem, ROW_ID, model)).willReturn(List.of(fileUploadResponse));

        assertThat(underTest.editTableColumns(listItem, ROW_ID, List.of(model))).containsExactly(fileUploadResponse);

        then(editTableColumnDeleter).should().deleteColumns(ROW_ID, List.of(model));
    }
}