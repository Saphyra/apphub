package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableHeadQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 32;
    private static final String CONTENT = "content";

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private TableHeadQueryService underTest;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content content;

    @Test
    void getTableHeads() {
        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(tableHead));
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHead.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(contentDao.findByParentValidated(TABLE_HEAD_ID)).willReturn(content);
        given(content.getContent()).willReturn(CONTENT);

        List<TableHeadModel> result = underTest.getTableHeads(LIST_ITEM_ID);

        assertThat(result).hasSize(1);
        TableHeadModel model = result.get(0);
        assertThat(model.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(model.getColumnIndex()).isEqualTo(COLUMN_INDEX);
        assertThat(model.getContent()).isEqualTo(CONTENT);
        assertThat(model.getType()).isEqualTo(ItemType.EXISTING);
    }
}