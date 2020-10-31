package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface ChecklistTableController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE)
    OneParamResponse<UUID> createChecklistTable(@RequestBody CreateChecklistTableRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EDIT_NOTEBOOK_CHECKLIST_TABLE)
    void editChecklistTable(@RequestBody EditChecklistTableRequest request, @PathVariable(name = "listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_NOTEBOOK_CHECKLIST_TABLE)
    ChecklistTableResponse getChecklistTable(@PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.UPDATE_CHECKLIST_TABLE_ROW_STATUS)
    void setChecklistTableRowStatus(@PathVariable("listItemId") UUID listItemId, @PathVariable("rowIndex") Integer rowIndex, @RequestBody OneParamRequest<Boolean> status);
}
