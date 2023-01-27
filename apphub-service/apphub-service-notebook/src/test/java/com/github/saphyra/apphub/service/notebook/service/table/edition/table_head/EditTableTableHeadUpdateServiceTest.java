package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditTableTableHeadUpdateServiceTest {
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String NEW_VALUE = "new-value";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @InjectMocks
    private EditTableTableHeadUpdateService underTest;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content content;

    @Test
    public void process() {
        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead));
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);

        given(contentDao.findByParentValidated(TABLE_HEAD_ID)).willReturn(content);

        List<KeyValuePair<String>> columnNames = Arrays.asList(
            new KeyValuePair<>(null, "asd"),
            new KeyValuePair<>(TABLE_HEAD_ID, NEW_VALUE)
        );

        underTest.process(columnNames, LIST_ITEM_ID);

        verify(tableHead).setColumnIndex(1);
        verify(tableHeadDao).save(tableHead);

        verify(content).setContent(NEW_VALUE);
        verify(contentDao).save(content);
    }
}