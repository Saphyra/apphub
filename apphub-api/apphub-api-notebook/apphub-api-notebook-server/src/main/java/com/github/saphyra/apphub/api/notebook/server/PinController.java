package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.NotebookEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface PinController {
    @PostMapping(NotebookEndpoints.NOTEBOOK_PIN_LIST_ITEM)
    void pinListItem(@PathVariable("listItemId") UUID listItemId, @RequestBody OneParamRequest<Boolean> pinned, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(NotebookEndpoints.NOTEBOOK_GET_PINNED_ITEMS)
    List<NotebookView> getPinnedItems(@RequestParam(value = "pinGroupId", required = false) UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(NotebookEndpoints.NOTEBOOK_CREATE_PIN_GROUP)
    List<PinGroupResponse> createPinGroup(@RequestBody OneParamRequest<String> groupName, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_RENAME_PIN_GROUP)
    List<PinGroupResponse> renamePinGroup(@RequestBody OneParamRequest<String> groupName, @PathVariable("pinGroupId") UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(NotebookEndpoints.NOTEBOOK_GET_PIN_GROUPS)
    List<PinGroupResponse> getPinGroups(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(NotebookEndpoints.NOTEBOOK_DELETE_PIN_GROUP)
    List<PinGroupResponse> deletePinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_ADD_ITEM_TO_PIN_GROUP)
    List<NotebookView> addItemToPinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(NotebookEndpoints.NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP)
    List<NotebookView> removeItemFromPinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(NotebookEndpoints.NOTEBOOK_PIN_GROUP_OPENED)
    List<PinGroupResponse> pinGroupOpened(@PathVariable("pinGroupId") UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
