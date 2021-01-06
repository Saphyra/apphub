package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.CheckedChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edition.EditChecklistItemService;
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
public class ChecklistControllerImplTest {
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
    private CheckedChecklistItemDeletionService checkedChecklistItemDeletionService;

    @InjectMocks
    private ChecklistControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateChecklistItemRequest createChecklistItemRequest;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private EditChecklistItemRequest editChecklistItemRequest;

    @Test
    public void createChecklistItem() {
        given(checklistCreationService.create(createChecklistItemRequest, USER_ID)).willReturn(LIST_ITEM_ID);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        OneParamResponse<UUID> result = underTest.createChecklistItem(createChecklistItemRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editChecklistItem() {
        underTest.editChecklistItem(editChecklistItemRequest, LIST_ITEM_ID);

        verify(editChecklistItemService).edit(editChecklistItemRequest, LIST_ITEM_ID);
    }

    @Test
    public void getChecklistItem() {
        given(checklistItemQueryService.query(LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.getChecklistItem(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    public void updateStatus() {
        underTest.updateStatus(new OneParamRequest<>(true), CHECKLIST_ITEM_ID);

        verify(checklistItemStatusUpdateService).update(CHECKLIST_ITEM_ID, true);
    }

    @Test
    public void deleteCheckedItems() {
        underTest.deleteCheckedItems(LIST_ITEM_ID);

        verify(checkedChecklistItemDeletionService).deleteCheckedItems(LIST_ITEM_ID);
    }
}