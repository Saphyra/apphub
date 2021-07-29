package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface ListItemController {
    @RequestMapping(method = RequestMethod.DELETE, path = Endpoints.NOTEBOOK_DELETE_LIST_ITEM)
    void deleteListItem(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_EDIT_LIST_ITEM)
    void editListItem(@RequestBody EditListItemRequest request, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_CLONE_LIST_ITEM)
    void cloneListItem(@PathVariable("listItemId") UUID listItemId);

    @PostMapping(Endpoints.NOTEBOOK_PIN_LIST_ITEM)
    void pinListItem(@PathVariable("listItemId") UUID listItemId, @RequestBody OneParamRequest<Boolean> pinned, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.NOTEBOOK_GET_PINNED_ITEMS)
    List<NotebookView> getPinnedItems(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
