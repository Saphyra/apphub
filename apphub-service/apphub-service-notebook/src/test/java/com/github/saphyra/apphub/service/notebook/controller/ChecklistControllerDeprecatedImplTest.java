package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequestDeprecated;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemsOrderService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.edition.EditChecklistItemService;
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
public class ChecklistControllerDeprecatedImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistCreationService checklistCreationService;

    @Mock
    private EditChecklistItemService editChecklistItemService;

    @Mock
    private ChecklistItemQueryService checklistItemQueryService;

    @Mock
    private ChecklistItemStatusUpdateService checklistItemStatusUpdateService;

    @Mock
    private ChecklistItemDeletionService checklistItemDeletionService;

    @Mock
    private ChecklistItemsOrderService checklistItemsOrderService;

    @InjectMocks
    private ChecklistControllerDeprecatedImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateChecklistRequestDeprecated createChecklistRequestDeprecated;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private EditChecklistItemRequest editChecklistItemRequest;

    @Test
    public void createChecklistItem() {
        given(checklistCreationService.create(createChecklistRequestDeprecated, USER_ID)).willReturn(LIST_ITEM_ID);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        OneParamResponse<UUID> result = underTest.createChecklist(createChecklistRequestDeprecated, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editChecklistItem() {
        given(checklistItemQueryService.query(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.editChecklist(editChecklistItemRequest, LIST_ITEM_ID);

        verify(editChecklistItemService).edit(editChecklistItemRequest, LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    public void getChecklistItem() {
        given(checklistItemQueryService.query(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.getChecklist(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    public void updateStatus() {
        underTest.updateStatus(new OneParamRequest<>(true), CHECKLIST_ITEM_ID);

        verify(checklistItemStatusUpdateService).update(CHECKLIST_ITEM_ID, true);
    }

    @Test
    public void deleteCheckedItems() {
        given(checklistItemQueryService.query(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.deleteCheckedItems(LIST_ITEM_ID);

        verify(checklistItemDeletionService).deleteCheckedItems(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    public void orderItems() {
        given(checklistItemQueryService.query(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.orderItems(LIST_ITEM_ID);

        verify(checklistItemsOrderService).orderChecklistItems(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    void deleteChecklistItem() {
        underTest.deleteChecklistItem(CHECKLIST_ITEM_ID, accessTokenHeader);

        verify(checklistItemDeletionService).delete(CHECKLIST_ITEM_ID);
    }
}