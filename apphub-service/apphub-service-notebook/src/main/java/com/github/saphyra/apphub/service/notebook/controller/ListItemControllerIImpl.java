package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.notebook.server.ListItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.ArchiveService;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemEditionService;
import com.github.saphyra.apphub.service.notebook.service.PinService;
import com.github.saphyra.apphub.service.notebook.service.clone.ListItemCloneService;
import com.github.saphyra.apphub.service.notebook.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class ListItemControllerIImpl implements ListItemController {
    private final ListItemCloneService listItemCloneService;
    private final ListItemDeletionService listItemDeletionService;
    private final ListItemEditionService listItemEditionService;
    private final PinService pinService;
    private final SearchService searchService;
    private final ArchiveService archiveService;

    @Override
    public void deleteListItem(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete listItem with id {}", accessTokenHeader.getUserId(), listItemId);
        listItemDeletionService.deleteListItem(listItemId, accessTokenHeader.getUserId());
    }

    @Override
    public void editListItem(EditListItemRequest request, UUID listItemId) {
        log.info("Editing listItem {}", listItemId);
        listItemEditionService.edit(listItemId, request);
    }

    @Override
    public void cloneListItem(UUID listItemId) {
        log.info("Cloning listItem {}", listItemId);
        listItemCloneService.clone(listItemId);
    }

    @Override
    public void pinListItem(UUID listItemId, OneParamRequest<Boolean> pinned, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change pin status of list item {}", accessTokenHeader.getUserId(), listItemId);
        pinService.pinListItem(listItemId, pinned.getValue());
    }

    @Override
    public List<NotebookView> getPinnedItems(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants wo query his pinned items", accessTokenHeader.getUserId());
        return pinService.getPinnedItems(accessTokenHeader.getUserId());
    }

    @Override
    public List<NotebookView> search(OneParamRequest<String> query, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to search for item(s).", accessTokenHeader.getUserId());
        return searchService.search(accessTokenHeader.getUserId(), query.getValue());
    }

    @Override
    public void archive(OneParamRequest<Boolean> archived, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to archive an item.", accessTokenHeader.getUserId());
        archiveService.archive(listItemId, archived.getValue());
    }
}
