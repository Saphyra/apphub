package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.api.notebook.model.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CloneUtilTest {
    private static final UUID ORIGINAL_PARENT = UUID.randomUUID();
    private static final UUID ORIGINAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer ORDER = 2342;
    private static final UUID NEW_ID = UUID.randomUUID();
    private static final UUID NEW_PARENT = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 3214;
    private static final Integer ROW_INDEX = 3654;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CloneUtil underTest;

    @BeforeEach
    public void setUp() {
        given(idGenerator.randomUuid()).willReturn(NEW_ID);
    }

    @Test
    public void cloneChecklistItem() {
        ChecklistItem checklistItem = ChecklistItem.builder()
            .checklistItemId(ORIGINAL_ID)
            .userId(USER_ID)
            .parent(ORIGINAL_PARENT)
            .order(ORDER)
            .checked(true)
            .build();

        ChecklistItem result = underTest.clone(NEW_PARENT, checklistItem);

        assertThat(result.getChecklistItemId()).isEqualTo(NEW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(NEW_PARENT);
        assertThat(result.getOrder()).isEqualTo(ORDER);
        assertThat(result.getChecked()).isTrue();
    }

    @Test
    public void cloneTableHead() {
        TableHead tableHead = TableHead.builder()
            .tableHeadId(ORIGINAL_ID)
            .userId(USER_ID)
            .parent(ORIGINAL_PARENT)
            .columnIndex(COLUMN_INDEX)
            .build();

        TableHead result = underTest.clone(NEW_PARENT, tableHead);

        assertThat(result.getTableHeadId()).isEqualTo(NEW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(NEW_PARENT);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }

    @Test
    public void cloneTableJoin() {
        TableJoin tableJoin = TableJoin.builder()
            .tableJoinId(ORIGINAL_ID)
            .userId(USER_ID)
            .parent(ORIGINAL_PARENT)
            .rowIndex(ROW_INDEX)
            .columnIndex(COLUMN_INDEX)
            .columnType(ColumnType.EMPTY)
            .build();

        TableJoin result = underTest.clone(NEW_PARENT, tableJoin);

        assertThat(result.getTableJoinId()).isEqualTo(NEW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(NEW_PARENT);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }

    @Test
    public void cloneChecklistTableRow() {
        ChecklistTableRow checklistTableRow = ChecklistTableRow.builder()
            .rowId(ORIGINAL_ID)
            .userId(USER_ID)
            .parent(ORIGINAL_PARENT)
            .rowIndex(ROW_INDEX)
            .checked(true)
            .build();

        ChecklistTableRow result = underTest.clone(NEW_PARENT, checklistTableRow);

        assertThat(result.getRowId()).isEqualTo(NEW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(NEW_PARENT);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.isChecked()).isTrue();
    }
}