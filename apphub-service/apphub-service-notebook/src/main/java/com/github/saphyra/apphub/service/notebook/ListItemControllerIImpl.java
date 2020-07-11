package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.api.notebook.server.ListItemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ListItemControllerIImpl implements ListItemController {
    private final ListItemDeletionService listItemDeletionService;

    @Override
    public void deleteListItem(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete listItem with id {}", accessTokenHeader.getUserId(), listItemId);
        listItemDeletionService.deleteListItem(listItemId, accessTokenHeader.getUserId());
    }
}
