package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.feature.notebook.server.ListItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.ArchiveService;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemEditionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemQueryService;
import com.github.saphyra.apphub.service.notebook.service.SearchService;
import com.github.saphyra.apphub.service.notebook.service.clone.ListItemCloneService;
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
    private final SearchService searchService;
    private final ArchiveService archiveService;
    private final ListItemQueryService listItemQueryService;

    @Override
    public NotebookView findListItem(UUID listItemId, AccessToken accessToken) {
        return listItemQueryService.findListItem(accessToken.getUserId(), listItemId);
    }

    @Override
    public void deleteListItem(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to delete listItem with id {}", accessToken.getUserId(), listItemId);
        listItemDeletionService.deleteListItem(listItemId, accessToken.getUserId());
    }

    @Override
    public void editListItem(EditListItemRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to edit listItem {}", accessToken.getUserId(), listItemId);
        listItemEditionService.edit(accessToken.getUserId(), listItemId, request);
    }

    @Override
    public void moveListItem(OneParamRequest<UUID> parent, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to move listItem with id {} to parent {}", accessToken.getUserId(), listItemId, parent);
        listItemEditionService.moveListItem(accessToken.getUserId(), listItemId, parent.getValue());
    }

    @Override
    public void cloneListItem(UUID listItemId) {
        log.info("Cloning listItem {}", listItemId);
        listItemCloneService.clone(listItemId);
    }

    @Override
    public List<NotebookView> search(OneParamRequest<String> query, AccessToken accessToken) {
        log.info("{} wants to search for item(s).", accessToken.getUserId());
        return searchService.search(accessToken.getUserId(), query.getValue());
    }

    @Override
    public void archive(OneParamRequest<Boolean> archived, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to archive an item.", accessToken.getUserId());
        archiveService.archive(accessToken.getUserId(), listItemId, archived.getValue());
    }
}
