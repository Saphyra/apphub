package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableHeadFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditTableTableHeadCreationServiceTest {
    private static final String VALUE_1 = "value-1";
    private static final String VALUE_2 = "value-2";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @InjectMocks
    private EditTableTableHeadCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content content;

    @Test
    public void process() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);

        KeyValuePair<String> pair1 = new KeyValuePair<>(null, VALUE_1);
        KeyValuePair<String> pair2 = new KeyValuePair<>(UUID.randomUUID(), VALUE_2);

        given(tableHeadFactory.create(LIST_ITEM_ID, VALUE_1, 0, USER_ID)).willReturn(new BiWrapper<>(tableHead, content));

        underTest.process(Arrays.asList(pair1, pair2), listItem);

        verify(tableHeadDao).save(tableHead);
        verify(contentDao).save(content);
    }
}