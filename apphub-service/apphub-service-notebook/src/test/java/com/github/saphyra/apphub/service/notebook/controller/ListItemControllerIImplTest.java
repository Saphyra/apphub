package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.ArchiveService;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemEditionService;
import com.github.saphyra.apphub.service.notebook.service.PinService;
import com.github.saphyra.apphub.service.notebook.service.clone.ListItemCloneService;
import com.github.saphyra.apphub.service.notebook.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ListItemControllerIImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SEARCH_TEXT = "search-text";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemDeletionService listItemDeletionService;

    @Mock
    private ListItemEditionService listItemEditionService;

    @Mock
    private ListItemCloneService listItemCloneService;

    @Mock
    private PinService pinService;

    @Mock
    private SearchService searchService;

    @Mock
    private ArchiveService archiveService;

    @InjectMocks
    private ListItemControllerIImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditListItemRequest editListItemRequest;

    @Mock
    private NotebookView notebookView;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void deleteListItem() {
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

    @Test
    public void pinListItem() {
        underTest.pinListItem(LIST_ITEM_ID, new OneParamRequest<>(true), accessTokenHeader);

        verify(pinService).pinListItem(LIST_ITEM_ID, true);
    }

    @Test
    public void getPinnedItems() {
        given(pinService.getPinnedItems(USER_ID)).willReturn(Arrays.asList(notebookView));

        List<NotebookView> result = underTest.getPinnedItems(accessTokenHeader);

        assertThat(result).containsExactly(notebookView);
    }

    @Test
    public void search() {
        given(searchService.search(USER_ID, SEARCH_TEXT)).willReturn(Arrays.asList(notebookView));

        List<NotebookView> result = underTest.search(new OneParamRequest<>(SEARCH_TEXT), accessTokenHeader);

        assertThat(result).containsExactly(notebookView);
    }

    @Test
    public void archive() {
        underTest.archive(new OneParamRequest<>(true), LIST_ITEM_ID, accessTokenHeader);

        verify(archiveService).archive(LIST_ITEM_ID, true);
    }

    @Test
    public void moveListItem() {
        underTest.moveListItem(new OneParamRequest<>(PARENT), LIST_ITEM_ID);

        verify(listItemEditionService).moveListItem(LIST_ITEM_ID, PARENT);
    }
}