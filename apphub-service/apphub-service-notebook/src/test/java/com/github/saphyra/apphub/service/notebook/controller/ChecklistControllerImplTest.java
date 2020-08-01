package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edit.EditChecklistItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistCreationService checklistCreationService;

    @Mock
    private EditChecklistItemService editChecklistItemService;

    @InjectMocks
    private ChecklistControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateChecklistItemRequest createChecklistItemRequest;

    @Mock
    private ChecklistItemNodeRequest checklistItemNodeRequest;

    @Test
    public void createChecklistItem() {
        given(checklistCreationService.create(createChecklistItemRequest, USER_ID)).willReturn(LIST_ITEM_ID);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        OneParamResponse<UUID> result = underTest.createChecklistItem(createChecklistItemRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editChecklistItem() {
        underTest.editChecklistItem(Arrays.asList(checklistItemNodeRequest), LIST_ITEM_ID);

        verify(editChecklistItemService).edit(Arrays.asList(checklistItemNodeRequest), LIST_ITEM_ID);
    }
}