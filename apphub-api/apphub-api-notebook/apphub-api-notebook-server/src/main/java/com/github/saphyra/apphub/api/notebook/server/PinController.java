package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
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
    @PostMapping(Endpoints.NOTEBOOK_PIN_LIST_ITEM)
    void pinListItem(@PathVariable("listItemId") UUID listItemId, @RequestBody OneParamRequest<Boolean> pinned, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.NOTEBOOK_GET_PINNED_ITEMS)
        //TODO api test (new query param: pinGroupId)
    List<NotebookView> getPinnedItems(@RequestParam(value = "pinGroupId", required = false) UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(Endpoints.NOTEBOOK_CREATE_PIN_GROUP)
        //TODO api test
    List<PinGroupResponse> createPinGroup(@RequestBody OneParamRequest<String> groupName, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.NOTEBOOK_RENAME_PIN_GROUP)
        //TODO api test
    List<PinGroupResponse> renamePinGroup(@RequestBody OneParamRequest<String> groupName, @PathVariable("pinGroupId") UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.NOTEBOOK_GET_PIN_GROUPS)
        //TODO api test
    List<PinGroupResponse> getPinGroups(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.NOTEBOOK_DELETE_PIN_GROUP)
        //TODO api test
    List<PinGroupResponse> deletePinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.NOTEBOOK_ADD_ITEM_TO_PIN_GROUP)
        //TODO api test
    List<NotebookView> addItemToPinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP)
        //TODO api test
    List<NotebookView> removeItemFromPinGroup(@PathVariable("pinGroupId") UUID pinGroupId, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
