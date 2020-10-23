package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.notebook.server.ListItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.ListItemEditionService;
import com.github.saphyra.apphub.service.notebook.service.clone.ListItemCloneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class ListItemControllerIImpl implements ListItemController {
    private final ListItemCloneService listItemCloneService;
    private final ListItemDeletionService listItemDeletionService;
    private final ListItemEditionService listItemEditionService;

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
    //TODO BE test
    //TODO api test
    //TODO int test
    public void cloneListItem(UUID listItemId) {
        log.info("Cloning listItem {}", listItemId);
        listItemCloneService.clone(listItemId);
    }
}
