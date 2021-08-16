package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ContentFactoryTest {
    private static final String CONTENT = "content";
    private static final UUID CONTENT_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ContentFactory underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CONTENT_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);

        Content result = underTest.create(listItem, CONTENT);

        assertThat(result.getContentId()).isEqualTo(CONTENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getContent()).isEqualTo(CONTENT);
    }

}