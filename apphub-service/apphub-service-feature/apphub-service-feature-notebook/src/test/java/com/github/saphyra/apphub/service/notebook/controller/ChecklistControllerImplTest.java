package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemCrudService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist.EditChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.OrderChecklistItemsService;
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
class ChecklistControllerImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private ChecklistCreationService checklistCreationService;

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private OrderChecklistItemsService orderChecklistItemsService;

    @Mock
    private EditChecklistService editChecklistService;

    @Mock
    private ChecklistItemCrudService checklistItemCrudService;

    @InjectMocks
    private ChecklistControllerImpl underTest;

    @Mock
    private CreateChecklistRequest createChecklistRequest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private EditChecklistRequest editChecklistRequest;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private AddChecklistItemRequest addChecklistItemRequest;

    @Test
    void createChecklist() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(checklistCreationService.create(USER_ID, createChecklistRequest)).willReturn(LIST_ITEM_ID);

        assertThat(underTest.createChecklist(createChecklistRequest, accessToken))
            .extracting(OneParamResponse::getValue)
            .isEqualTo(LIST_ITEM_ID);
    }

    @Test
    void editChecklist() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        given(editChecklistService.edit(USER_ID, LIST_ITEM_ID, editChecklistRequest)).willReturn(checklistResponse);

        assertThat(underTest.editChecklist(editChecklistRequest, LIST_ITEM_ID, accessToken)).isEqualTo(checklistResponse);
    }

    @Test
    void getChecklist() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.getChecklist(LIST_ITEM_ID, accessToken)).isEqualTo(checklistResponse);
    }

    @Test
    void updateStatus() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.updateStatus(new OneParamRequest<>(true), LIST_ITEM_ID, CHECKLIST_ITEM_ID, accessToken);

        then(checklistItemCrudService).should().updateStatus(USER_ID, LIST_ITEM_ID, CHECKLIST_ITEM_ID, true);
    }

    @Test
    void deleteCheckedItem() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.deleteChecklistItem(LIST_ITEM_ID, CHECKLIST_ITEM_ID, accessToken);

        then(checklistItemCrudService).should().deleteChecklistItem(USER_ID, LIST_ITEM_ID, CHECKLIST_ITEM_ID);
    }

    @Test
    void deleteCheckedItems() {
        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.deleteCheckedItems(LIST_ITEM_ID, accessToken)).isEqualTo(checklistResponse);

        then(checklistItemCrudService).should().deleteCheckedItems(USER_ID, LIST_ITEM_ID);
    }

    @Test
    void orderItems() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(orderChecklistItemsService.orderItems(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.orderItems(LIST_ITEM_ID, accessToken)).isEqualTo(checklistResponse);
    }

    @Test
    void editChecklistItem() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.editChecklistItem(new OneParamRequest<>(CONTENT), LIST_ITEM_ID, CHECKLIST_ITEM_ID, accessToken);

        then(checklistItemCrudService).should().updateContent(USER_ID, LIST_ITEM_ID, CHECKLIST_ITEM_ID, CONTENT);
    }

    @Test
    void addChecklistItem() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.addChecklistItem(addChecklistItemRequest, LIST_ITEM_ID, accessToken)).isEqualTo(checklistResponse);

        then(checklistItemCrudService).should().addChecklistItem(USER_ID, LIST_ITEM_ID, addChecklistItemRequest);
    }
}