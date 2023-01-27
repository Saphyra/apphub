package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface TableController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.NOTEBOOK_CREATE_TABLE)
    OneParamResponse<UUID> createTable(@RequestBody CreateTableRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_EDIT_TABLE)
    void editTable(@RequestBody EditTableRequest request, @PathVariable(name = "listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.NOTEBOOK_GET_TABLE)
    TableResponse getTable(@PathVariable("listItemId") UUID listItemId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_CONVERT_TABLE_TO_CHECKLIST_TABLE)
    void convertToChecklistTable(@PathVariable("listItemId") UUID listItemId);
}
