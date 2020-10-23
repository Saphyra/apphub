package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface ListItemController {
    @RequestMapping(method = RequestMethod.DELETE, path = Endpoints.DELETE_NOTEBOOK_LIST_ITEM)
    void deleteListItem(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EDIT_NOTEBOOK_LIST_ITEM)
    void editListItem(@RequestBody EditListItemRequest request, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.CLONE_NOTEBOOK_LIST_ITEM)
    void cloneListItem(@PathVariable("listItemId") UUID listItemId);
}
