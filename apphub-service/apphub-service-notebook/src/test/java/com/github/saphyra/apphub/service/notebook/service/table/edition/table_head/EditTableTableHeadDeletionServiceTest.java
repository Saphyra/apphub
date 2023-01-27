package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
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
public class EditTableTableHeadDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID_1 = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID_2 = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @InjectMocks
    private EditTableTableHeadDeletionService underTest;

    @Mock
    private TableHead tableHead1;

    @Mock
    private TableHead tableHead2;

    @Test
    public void process() {
        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead1, tableHead2));
        given(tableHead1.getTableHeadId()).willReturn(TABLE_HEAD_ID_1);
        given(tableHead2.getTableHeadId()).willReturn(TABLE_HEAD_ID_2);

        List<KeyValuePair<String>> pairs = Arrays.asList(
            new KeyValuePair<>(null, "asd"),
            new KeyValuePair<>(TABLE_HEAD_ID_1, "asd")
        );

        underTest.process(pairs, LIST_ITEM_ID);

        verify(contentDao).deleteByParent(TABLE_HEAD_ID_2);
        verify(tableHeadDao).delete(tableHead2);
    }
}