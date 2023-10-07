package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequestDeprecated;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Deprecated
public interface ChecklistControllerDeprecated {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.NOTEBOOK_CREATE_CHECKLIST)
    OneParamResponse<UUID> createChecklist(@RequestBody CreateChecklistRequestDeprecated request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_EDIT_CHECKLIST)
    ChecklistResponse editChecklist(@RequestBody EditChecklistItemRequest request, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.NOTEBOOK_GET_CHECKLIST)
    ChecklistResponse getChecklist(@PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS)
    void updateStatus(@RequestBody OneParamRequest<Boolean> request, @PathVariable("checklistItemId") UUID checklistItemId);

    @DeleteMapping(Endpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM)
    void deleteChecklistItem(@PathVariable("checklistItemId") UUID checklistItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED)
    ChecklistResponse deleteCheckedItems(@PathVariable("listItemId") UUID listItemId);

    @PostMapping(Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS)
    ChecklistResponse orderItems(@PathVariable("listItemId") UUID listItemId);
}
