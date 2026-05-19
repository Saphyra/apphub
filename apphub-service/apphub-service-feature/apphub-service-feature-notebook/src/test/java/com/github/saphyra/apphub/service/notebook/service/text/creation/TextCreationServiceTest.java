package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TextCreationServiceTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private CreateTextRequestValidator createTextRequestValidator;

    @Mock
    private DeprecatedListItemDao listItemDao;

    @Mock
    private DeprecatedListItemFactory listItemFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TextCreationService underTest;

    @Mock
    private DeprecatedListItem listItem;

    @Mock
    private Content content;

    @Test
    public void create() {
        CreateTextRequest request = CreateTextRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.TEXT)).willReturn(listItem);
        given(contentFactory.create(listItem, CONTENT)).willReturn(content);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        UUID result = underTest.create(request, USER_ID);

        assertThat(result).isEqualTo(LIST_ITEM_ID);

        verify(createTextRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        verify(contentDao).save(content);
    }
}