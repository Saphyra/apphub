package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface ChecklistController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM)
    OneParamResponse<UUID> createChecklistItem(@RequestBody CreateChecklistItemRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_EDIT_CHECKLIST_ITEM)
    void editChecklistItem(@RequestBody EditChecklistItemRequest request, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.NOTEBOOK_GET_CHECKLIST_ITEM)
    ChecklistResponse getChecklistItem(@PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS)
    void updateStatus(@RequestBody OneParamRequest<Boolean> request, @PathVariable("checklistItemId") UUID checklistItemId);

    @DeleteMapping(Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST)
    void deleteCheckedItems(@PathVariable("listItemId") UUID listItemId);

    @PostMapping(Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS)
    void orderItems(@PathVariable("listItemId") UUID listItemId);
}
