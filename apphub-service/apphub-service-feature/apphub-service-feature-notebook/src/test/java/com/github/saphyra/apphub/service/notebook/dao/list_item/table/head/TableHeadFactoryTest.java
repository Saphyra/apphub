package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

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
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final int INDEX = 2;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TableHeadFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(TABLE_HEAD_ID);

        TableHead result = underTest.create(INDEX);

        assertThat(result.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }

    @Test
    void clone_tableHead() {
        given(idGenerator.randomUuid()).willReturn(TABLE_HEAD_ID);

        TableHead original = TableHead.builder()
            .tableHeadId(UUID.randomUUID())
            .index(INDEX)
            .build();

        TableHead result = underTest.clone(original);

        assertThat(result.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }
}

