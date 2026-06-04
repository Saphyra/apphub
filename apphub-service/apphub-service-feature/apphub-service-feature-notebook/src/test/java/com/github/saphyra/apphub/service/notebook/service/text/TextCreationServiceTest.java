package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TextCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private TextValidator textValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @InjectMocks
    private TextCreationService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void create() {
        CreateTextRequest request = CreateTextRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(listItemFactory.create(USER_ID, PARENT, TITLE, ListItemType.TEXT, CONTENT)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        UUID result = underTest.create(request, USER_ID);

        then(textValidator).should().validate(USER_ID, request);
        then(listItemDao).should().save(listItem);
        assertThat(result).isEqualTo(LIST_ITEM_ID);
    }
}

