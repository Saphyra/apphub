package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.util.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistTableRowFactoryTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final int ROW_INDEX = 2534;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChecklistTableRowFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUUID()).willReturn(ROW_ID);

        ChecklistTableRow result = underTest.create(USER_ID, PARENT, ROW_INDEX, true);

        assertThat(result.getRowId()).isEqualTo(ROW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.isChecked()).isTrue();
    }
}