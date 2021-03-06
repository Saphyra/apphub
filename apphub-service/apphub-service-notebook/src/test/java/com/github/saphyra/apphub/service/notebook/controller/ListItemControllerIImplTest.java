package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemEditionService;
import com.github.saphyra.apphub.service.notebook.service.clone.ListItemCloneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemControllerIImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDeletionService listItemDeletionService;

    @Mock
    private ListItemEditionService listItemEditionService;

    @Mock
    private ListItemCloneService listItemCloneService;

    @InjectMocks
    private ListItemControllerIImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditListItemRequest editListItemRequest;

    @Test
    public void deleteListItem() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.deleteListItem(LIST_ITEM_ID, accessTokenHeader);

        verify(listItemDeletionService).deleteListItem(LIST_ITEM_ID, USER_ID);
    }

    @Test
    public void editListItem() {
        underTest.editListItem(editListItemRequest, LIST_ITEM_ID);

        verify(listItemEditionService).edit(LIST_ITEM_ID, editListItemRequest);
    }

    @Test
    public void cloneListItem() {
        underTest.cloneListItem(LIST_ITEM_ID);

        verify(listItemCloneService).clone(LIST_ITEM_ID);
    }
}