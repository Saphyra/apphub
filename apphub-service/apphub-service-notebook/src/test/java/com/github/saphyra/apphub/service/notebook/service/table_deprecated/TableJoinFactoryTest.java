package com.github.saphyra.apphub.service.notebook.service.table_deprecated;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TableJoinFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String COLUMN_VALUE = "column-value";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final int ROW_INDEX = 3245;
    private static final int COLUMN_INDEX = 36;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TableJoinFactory underTest;

    @Mock
    private Content content;

    @Test
    public void create_withContent() {
        given(idGenerator.randomUuid()).willReturn(TABLE_JOIN_ID);
        given(contentFactory.create(LIST_ITEM_ID, TABLE_JOIN_ID, USER_ID, COLUMN_VALUE)).willReturn(content);

        List<BiWrapper<TableJoin, Content>> result = underTest.create(LIST_ITEM_ID, Arrays.asList(Arrays.asList(COLUMN_VALUE)), USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntity1().getTableJoinId()).isEqualTo(TABLE_JOIN_ID);
        assertThat(result.get(0).getEntity1().getUserId()).isEqualTo(USER_ID);
        assertThat(result.get(0).getEntity1().getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.get(0).getEntity1().getRowIndex()).isEqualTo(0);
        assertThat(result.get(0).getEntity1().getColumnIndex()).isEqualTo(0);
        assertThat(result.get(0).getEntity1().getColumnType()).isEqualTo(ColumnType.TEXT);
        assertThat(result.get(0).getEntity2()).isEqualTo(content);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(TABLE_JOIN_ID);

        TableJoin result = underTest.create(LIST_ITEM_ID, ROW_INDEX, COLUMN_INDEX, USER_ID, ColumnType.COLOR);

        assertThat(result.getTableJoinId()).isEqualTo(TABLE_JOIN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.getColumnIndex()).isEqualTo(COLUMN_INDEX);
        assertThat(result.getColumnType()).isEqualTo(ColumnType.COLOR);
    }
}