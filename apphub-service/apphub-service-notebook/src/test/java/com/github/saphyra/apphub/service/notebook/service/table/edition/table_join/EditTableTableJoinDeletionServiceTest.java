package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
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
public class EditTableTableJoinDeletionServiceTest {
    private static final UUID TABLE_JOIN_ID_1 = UUID.randomUUID();
    private static final UUID TABLE_JOIN_ID_2 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @InjectMocks
    private EditTableTableJoinDeletionService underTest;

    @Mock
    private TableJoin tableJoin1;

    @Mock
    private TableJoin tableJoin2;

    @Test
    public void process() {
        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin1, tableJoin2));
        given(tableJoin1.getTableJoinId()).willReturn(TABLE_JOIN_ID_1);
        given(tableJoin2.getTableJoinId()).willReturn(TABLE_JOIN_ID_2);

        List<List<KeyValuePair<String>>> columns = Arrays.asList(
            Arrays.asList(
                new KeyValuePair<>(TABLE_JOIN_ID_1, "asd")
            )
        );

        underTest.process(columns, LIST_ITEM_ID);

        verify(tableJoinDao).delete(tableJoin2);
        verify(contentDao).deleteByParent(TABLE_JOIN_ID_2);
    }
}