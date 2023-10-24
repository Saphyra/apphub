package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
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
class EditChecklistServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";

    @Mock
    private EditChecklistRequestValidator editChecklistRequestValidator;

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private EditChecklistRowDeleter editChecklistRowDeleter;

    @Mock
    private EditChecklistRowSaver editChecklistRowSaver;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private EditChecklistService underTest;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private EditChecklistRequest request;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItemModel model;

    @Test
    void edit() {
        given(checklistQueryService.getChecklistResponse(LIST_ITEM_ID)).willReturn(checklistResponse);
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(request.getTitle()).willReturn(TITLE);
        given(request.getItems()).willReturn(List.of(model));

        ChecklistResponse result = underTest.edit(USER_ID, LIST_ITEM_ID, request);

        assertThat(result).isEqualTo(checklistResponse);

        then(editChecklistRequestValidator).should().validate(request);
        then(listItem).should().setTitle(TITLE);
        then(listItemDao).should().save(listItem);
        then(editChecklistRowDeleter).should().deleteRemovedItems(LIST_ITEM_ID, List.of(model));
        then(editChecklistRowSaver).should().saveItems(USER_ID, LIST_ITEM_ID, List.of(model));
    }
}