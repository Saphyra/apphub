package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private CreateChecklistRequestValidator createChecklistRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemCreationService checklistItemCreationService;

    @InjectMocks
    private ChecklistCreationService underTest;

    @Mock
    private CreateChecklistRequest request;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItemModel model;

    @Test
    void create() {
        given(request.getTitle()).willReturn(TITLE);
        given(request.getParent()).willReturn(PARENT);
        given(request.getItems()).willReturn(List.of(model));
        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.CHECKLIST)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        UUID result = underTest.create(USER_ID, request);

        then(createChecklistRequestValidator).should().validate(request);
        assertThat(result).isEqualTo(LIST_ITEM_ID);
        then(checklistItemCreationService).should().create(USER_ID, LIST_ITEM_ID, List.of(model));
    }
}