package com.github.saphyra.apphub.service.notebook.dao.table_head;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableHeadFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 34;
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TableHeadFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(TABLE_HEAD_ID);

        TableHead result = underTest.create(USER_ID, PARENT, COLUMN_INDEX);

        assertThat(result.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
    }
}