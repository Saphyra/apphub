package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface ChecklistController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM)
    OneParamResponse<UUID> createChecklistItem(@RequestBody CreateChecklistItemRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM)
    void editChecklistItem(@RequestBody List<ChecklistItemNodeRequest> request, @PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_NOTEBOOK_CHECKLIST_ITEM)
    ChecklistResponse getChecklistItem(@PathVariable("listItemId") UUID listItemId);
}
