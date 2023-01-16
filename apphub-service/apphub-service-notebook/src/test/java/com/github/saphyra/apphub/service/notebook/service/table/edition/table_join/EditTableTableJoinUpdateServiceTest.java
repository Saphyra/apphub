package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
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
public class EditTableTableJoinUpdateServiceTest {
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();
    private static final String NEW_CONTENT = "new-content";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @InjectMocks
    private EditTableTableJoinUpdateService underTest;

    @Mock
    private TableJoin tableJoin;

    @Mock
    private Content content;

    @Test
    public void process() {
        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin));
        given(tableJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);

        given(contentDao.findByParentValidated(TABLE_JOIN_ID)).willReturn(content);

        List<List<KeyValuePair<String>>> columns = Arrays.asList(
            Arrays.asList(
                new KeyValuePair<>(null, "asd"),
                new KeyValuePair<>(TABLE_JOIN_ID, NEW_CONTENT)
            )
        );

        underTest.process(columns, LIST_ITEM_ID);

        verify(tableJoin).setRowIndex(0);
        verify(tableJoin).setColumnIndex(1);
        verify(tableJoinDao).save(tableJoin);

        verify(content).setContent(NEW_CONTENT);
        verify(contentDao).save(content);
    }
}