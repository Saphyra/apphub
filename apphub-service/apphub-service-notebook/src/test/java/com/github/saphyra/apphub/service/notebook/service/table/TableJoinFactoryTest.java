package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.util.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TableJoinFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String COLUMN_VALUE = "column-value";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TableJoinFactory underTest;

    @Mock
    private Content content;

    @Test
    public void create() {
        given(idGenerator.randomUUID()).willReturn(TABLE_JOIN_ID);
        given(contentFactory.create(TABLE_JOIN_ID, USER_ID, COLUMN_VALUE)).willReturn(content);

        List<BiWrapper<TableJoin, Content>> result = underTest.create(LIST_ITEM_ID, Arrays.asList(Arrays.asList(COLUMN_VALUE)), USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntity1().getTableJoinId()).isEqualTo(TABLE_JOIN_ID);
        assertThat(result.get(0).getEntity1().getUserId()).isEqualTo(USER_ID);
        assertThat(result.get(0).getEntity1().getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.get(0).getEntity1().getRowIndex()).isEqualTo(0);
        assertThat(result.get(0).getEntity1().getColumnIndex()).isEqualTo(0);
        assertThat(result.get(0).getEntity2()).isEqualTo(content);
    }
}