package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LinkCreationServiceTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String URL = "url";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private LinkRequestValidator linkRequestValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @InjectMocks
    private LinkCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Test
    public void create() {
        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .url(URL)
            .build();

        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.LINK)).willReturn(listItem);
        given(contentFactory.create(listItem, URL)).willReturn(content);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        UUID result = underTest.create(request, USER_ID);

        verify(linkRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        verify(contentDao).save(content);
        assertThat(result).isEqualTo(LIST_ITEM_ID);
    }
}