package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.NotebookEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

public interface ListItemController {
    @GetMapping(NotebookEndpoints.NOTEBOOK_GET_LIST_ITEM)
    NotebookView findListItem(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.DELETE, path = NotebookEndpoints.NOTEBOOK_DELETE_LIST_ITEM)
    void deleteListItem(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = NotebookEndpoints.NOTEBOOK_EDIT_LIST_ITEM)
    void editListItem(@RequestBody EditListItemRequest request, @PathVariable("listItemId") UUID listItemId);

    @PostMapping(NotebookEndpoints.NOTEBOOK_MOVE_LIST_ITEM)
    void moveListItem(@RequestBody OneParamRequest<UUID> parent, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = NotebookEndpoints.NOTEBOOK_CLONE_LIST_ITEM)
    void cloneListItem(@PathVariable("listItemId") UUID listItemId);

    @PostMapping(NotebookEndpoints.NOTEBOOK_SEARCH)
    List<NotebookView> search(@RequestBody OneParamRequest<String> query, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_ARCHIVE_ITEM)
    void archive(@RequestBody OneParamRequest<Boolean> archived, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
