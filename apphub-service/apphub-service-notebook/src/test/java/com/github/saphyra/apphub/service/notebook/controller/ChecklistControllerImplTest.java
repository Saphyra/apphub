package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemAdditionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemContentUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.DeleteCheckedItemsOfChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.OrderChecklistItemsService;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edit.EditChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
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
    private ChecklistItemStatusUpdateService checklistItemStatusUpdateService;

    @Mock
    private ChecklistItemDeletionService checklistItemDeletionService;

    @Mock
    private DeleteCheckedItemsOfChecklistService deleteCheckedItemsOfChecklistService;

    @Mock
    private OrderChecklistItemsService orderChecklistItemsService;

    @Mock
    private EditChecklistService editChecklistService;

    @Mock
    private ChecklistItemAdditionService checklistItemAdditionService;

    @InjectMocks
    private ChecklistControllerImpl underTest;

    @Mock
    private CreateChecklistRequest createChecklistRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditChecklistRequest editChecklistRequest;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private ChecklistItemContentUpdateService checklistItemContentUpdateService;

    @Mock
    private AddChecklistItemRequest addChecklistItemRequest;

    @Test
    void createChecklist() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(checklistCreationService.create(USER_ID, createChecklistRequest)).willReturn(LIST_ITEM_ID);

        assertThat(underTest.createChecklist(createChecklistRequest, accessTokenHeader))
            .extracting(OneParamResponse::getValue)
            .isEqualTo(LIST_ITEM_ID);
    }

    @Test
    void editChecklist() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(editChecklistService.edit(USER_ID, LIST_ITEM_ID, editChecklistRequest)).willReturn(checklistResponse);

        assertThat(underTest.editChecklist(editChecklistRequest, LIST_ITEM_ID, accessTokenHeader)).isEqualTo(checklistResponse);
    }

    @Test
    void getChecklist() {
        given(checklistQueryService.getChecklistResponse(LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.getChecklist(LIST_ITEM_ID, accessTokenHeader)).isEqualTo(checklistResponse);
    }

    @Test
    void updateStatus() {
        underTest.updateStatus(new OneParamRequest<>(true), CHECKLIST_ITEM_ID, accessTokenHeader);

        then(checklistItemStatusUpdateService).should().updateStatus(CHECKLIST_ITEM_ID, true);
    }

    @Test
    void deleteCheckedItem() {
        underTest.deleteChecklistItem(CHECKLIST_ITEM_ID, accessTokenHeader);

        then(checklistItemDeletionService).should().deleteChecklistItem(CHECKLIST_ITEM_ID);
    }

    @Test
    void deleteCheckedItems() {
        given(deleteCheckedItemsOfChecklistService.deleteCheckedItems(LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.deleteCheckedItems(LIST_ITEM_ID, accessTokenHeader)).isEqualTo(checklistResponse);
    }

    @Test
    void orderItems() {
        given(orderChecklistItemsService.orderItems(LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.orderItems(LIST_ITEM_ID, accessTokenHeader)).isEqualTo(checklistResponse);
    }

    @Test
    void editChecklistItem() {
        underTest.editChecklistItem(new OneParamRequest<>(CONTENT), CHECKLIST_ITEM_ID, accessTokenHeader);

        then(checklistItemContentUpdateService).should().updateContent(CHECKLIST_ITEM_ID, CONTENT);
    }

    @Test
    void addChecklistItem() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(checklistQueryService.getChecklistResponse(LIST_ITEM_ID)).willReturn(checklistResponse);

        assertThat(underTest.addChecklistItem(addChecklistItemRequest, LIST_ITEM_ID, accessTokenHeader)).isEqualTo(checklistResponse);

        then(checklistItemAdditionService).should().addChecklistItem(USER_ID, LIST_ITEM_ID, addChecklistItemRequest);
    }
}