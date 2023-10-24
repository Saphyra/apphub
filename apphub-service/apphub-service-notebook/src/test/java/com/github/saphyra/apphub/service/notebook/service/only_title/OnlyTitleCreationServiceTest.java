package com.github.saphyra.apphub.service.notebook.service.only_title;

import com.github.saphyra.apphub.api.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
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
public class OnlyTitleCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private CreateOnlyTitleRequestValidator createOnlyTitleRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private OnlyTitleCreationService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void create() {
        CreateOnlyTitleRequest request = CreateOnlyTitleRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.ONLY_TITLE)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        UUID result = underTest.create(request, USER_ID);

        verify(createOnlyTitleRequestValidator).validate(request);
        verify(listItemDao).save(listItem);

        assertThat(result).isEqualTo(LIST_ITEM_ID);
    }
}