package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
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
public class TableQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String COLUMN_NAME = "column-name";
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final String COLUMN_VALUE = "column-value";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private ContentDao contentDao;


    @InjectMocks
    private TableQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableJoin tableJoin;

    @Mock
    private Content tableHeadContent;

    @Mock
    private Content tableJoinContent;

    @Test
    public void getTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getTitle()).willReturn(TITLE);

        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead));
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHead.getColumnIndex()).willReturn(0);

        given(contentDao.findByParentValidated(TABLE_HEAD_ID)).willReturn(tableHeadContent);
        given(tableHeadContent.getContent()).willReturn(COLUMN_NAME);

        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin));
        given(tableJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);
        given(tableJoin.getRowIndex()).willReturn(0);
        given(tableJoin.getColumnIndex()).willReturn(0);

        given(contentDao.findByParentValidated(TABLE_JOIN_ID)).willReturn(tableJoinContent);
        given(tableJoinContent.getContent()).willReturn(COLUMN_VALUE);

        TableResponse tableResponse = underTest.getTable(LIST_ITEM_ID);

        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);

        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);

        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getTableJoinId()).isEqualTo(TABLE_JOIN_ID);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
    }
}