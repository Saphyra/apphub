package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;


import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableJoinFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditTableTableJoinCreationServiceTest {
    private static final String CONTENT = "content";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private TableJoinFactory tableJoinFactory;

    @InjectMocks
    private EditTableTableJoinCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableJoin tableJoin;

    @Mock
    private Content content;

    @Test
    public void process() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(tableJoinFactory.create(LIST_ITEM_ID, CONTENT, 0, 1, USER_ID)).willReturn(new BiWrapper<>(tableJoin, content));

        List<List<KeyValuePair<String>>> columns = Arrays.asList(
            Arrays.asList(
                new KeyValuePair<>(UUID.randomUUID(), "asd"),
                new KeyValuePair<>(null, CONTENT)
            )
        );

        underTest.process(columns, listItem);

        verify(tableJoinDao).save(tableJoin);
        verify(contentDao).save(content);
    }
}