package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableRowStatusResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableHeadResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.ContentTableColumnResponseProvider;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.TableQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ChecklistTableQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final Integer ROW_INDEX = 2345;
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private TableQueryService tableQueryService;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @Mock
    private ContentTableColumnResponseProvider contentTableColumnResponseProvider;

    @InjectMocks
    private ChecklistTableQueryService underTest;

    @Mock
    private TableHeadResponse tableHeadResponse;

    @Mock
    private TableColumnResponse<String> tableColumnResponse;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Test
    public void getChecklistTable() {
        TableResponse<String> tableResponse = TableResponse.<String>builder()
            .title(TITLE)
            .parent(PARENT)
            .tableHeads(Arrays.asList(tableHeadResponse))
            .tableColumns(Arrays.asList(tableColumnResponse))
            .build();
        given(tableQueryService.getTable(LIST_ITEM_ID, contentTableColumnResponseProvider)).willReturn(tableResponse);

        given(checklistTableRowDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistTableRow));
        given(checklistTableRow.getRowIndex()).willReturn(ROW_INDEX);
        given(checklistTableRow.isChecked()).willReturn(true);
        given(checklistTableRow.getRowId()).willReturn(ROW_ID);

        ChecklistTableResponse result = underTest.getChecklistTable(LIST_ITEM_ID);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getTableHeads()).containsExactly(tableHeadResponse);
        assertThat(result.getTableColumns()).containsExactly(tableColumnResponse);
        assertThat(result.getRowStatus().get(ROW_INDEX)).extracting(ChecklistTableRowStatusResponse::getChecked).isEqualTo(true);
        assertThat(result.getRowStatus().get(ROW_INDEX)).extracting(ChecklistTableRowStatusResponse::getRowId).isEqualTo(ROW_ID);
    }
}