package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableColumnResponseProvider;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableQueryService;
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
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableColumnResponseProvider<String> tableColumnResponseProvider;

    @InjectMocks
    private TableQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content tableHeadContent;

    @Mock
    private TableColumnResponse<String> tableColumnResponse;

    @Test
    public void getTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getTitle()).willReturn(TITLE);
        given(listItem.getParent()).willReturn(PARENT);

        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead));
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHead.getColumnIndex()).willReturn(0);

        given(contentDao.findByParentValidated(TABLE_HEAD_ID)).willReturn(tableHeadContent);
        given(tableHeadContent.getContent()).willReturn(COLUMN_NAME);

        given(tableColumnResponseProvider.fetchTableColumns(LIST_ITEM_ID)).willReturn(Arrays.asList(tableColumnResponse));

        TableResponse<String> tableResponse = underTest.getTable(LIST_ITEM_ID, tableColumnResponseProvider);

        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getParent()).isEqualTo(PARENT);

        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);

        assertThat(tableResponse.getTableColumns()).containsExactly(tableColumnResponse);
    }
}