package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DeprecatedContentFactoryTest {
    private static final String CONTENT = "content";
    private static final UUID CONTENT_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private DeprecatedContentFactory underTest;

    @Mock
    private DeprecatedListItem listItem;

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