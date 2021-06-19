package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
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
public class TableHeadFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String COLUMN_NAME = "column-name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TableHeadFactory underTest;

    @Mock
    private Content content;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(TABLE_HEAD_ID);
        given(contentFactory.create(TABLE_HEAD_ID, USER_ID, COLUMN_NAME)).willReturn(content);

        List<BiWrapper<TableHead, Content>> result = underTest.create(LIST_ITEM_ID, Arrays.asList(COLUMN_NAME), USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntity1().getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(result.get(0).getEntity1().getUserId()).isEqualTo(USER_ID);
        assertThat(result.get(0).getEntity1().getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.get(0).getEntity1().getColumnIndex()).isEqualTo(0);
        assertThat(result.get(0).getEntity2()).isEqualTo(content);
    }
}