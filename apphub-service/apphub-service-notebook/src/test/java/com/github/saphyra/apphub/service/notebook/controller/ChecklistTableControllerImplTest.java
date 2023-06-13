package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.CheckedChecklistTableItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.creation.ChecklistTableCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.edition.ChecklistTableEditionService;
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
public class ChecklistTableControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private ChecklistTableCreationService checklistTableCreationService;

    @Mock
    private ChecklistTableEditionService checklistTableEditionService;

    @Mock
    private ChecklistTableQueryService checklistTableQueryService;

    @Mock
    private ChecklistTableStatusUpdateService checklistTableStatusUpdateService;

    @Mock
    private CheckedChecklistTableItemDeletionService checkedChecklistTableItemDeletionService;

    @InjectMocks
    private ChecklistTableControllerImpl underTest;

    @Mock
    private CreateChecklistTableRequest createChecklistTableRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditChecklistTableRequest editChecklistTableRequest;

    @Mock
    private ChecklistTableResponse checklistTableResponse;

    @Test
    public void createChecklistTable() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(checklistTableCreationService.create(USER_ID, createChecklistTableRequest)).willReturn(LIST_ITEM_ID);

        OneParamResponse<UUID> result = underTest.createChecklistTable(createChecklistTableRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editChecklistTable() {
        underTest.editChecklistTable(editChecklistTableRequest, LIST_ITEM_ID);

        verify(checklistTableEditionService).edit(LIST_ITEM_ID, editChecklistTableRequest);
    }

    @Test
    public void getChecklistTable() {
        given(checklistTableQueryService.getChecklistTable(LIST_ITEM_ID)).willReturn(checklistTableResponse);

        ChecklistTableResponse result = underTest.getChecklistTable(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistTableResponse);
    }

    @Test
    public void setChecklistTableRowStatus() {
        underTest.setChecklistTableRowStatus(ROW_ID, new OneParamRequest<>(true));

        verify(checklistTableStatusUpdateService).updateStatus(ROW_ID, true);
    }

    @Test
    public void deleteCheckedItems() {
        underTest.deleteCheckedItems(LIST_ITEM_ID);

        verify(checkedChecklistTableItemDeletionService).deleteCheckedItems(LIST_ITEM_ID);
    }
}