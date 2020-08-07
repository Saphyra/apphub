package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface TableController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_TABLE)
    OneParamResponse<UUID> createTable(@RequestBody CreateTableRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EDIT_NOTEBOOK_TABLE)
    void editTable(@RequestBody EditTableRequest request, @PathVariable(name = "listItemId") UUID listItemId);
}
