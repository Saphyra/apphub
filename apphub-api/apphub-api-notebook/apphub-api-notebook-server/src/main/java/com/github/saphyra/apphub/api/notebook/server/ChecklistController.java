package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.NotebookEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface ChecklistController {
    @PutMapping(NotebookEndpoints.NOTEBOOK_CREATE_CHECKLIST)
    OneParamResponse<UUID> createChecklist(@RequestBody CreateChecklistRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_EDIT_CHECKLIST)
    ChecklistResponse editChecklist(@RequestBody EditChecklistRequest request, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(NotebookEndpoints.NOTEBOOK_GET_CHECKLIST)
    ChecklistResponse getChecklist(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS)
    void updateStatus(@RequestBody OneParamRequest<Boolean> request, @PathVariable("checklistItemId") UUID checklistItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(NotebookEndpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM)
    void deleteChecklistItem(@PathVariable("checklistItemId") UUID checklistItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(NotebookEndpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED)
    ChecklistResponse deleteCheckedItems(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS)
    ChecklistResponse orderItems(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(NotebookEndpoints.NOTEBOOK_EDIT_CHECKLIST_ITEM)
    void editChecklistItem(@RequestBody OneParamRequest<String> content, @PathVariable("checklistItemId") UUID checklistItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(NotebookEndpoints.NOTEBOOK_ADD_CHECKLIST_ITEM)
    ChecklistResponse addChecklistItem(@RequestBody AddChecklistItemRequest request, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
