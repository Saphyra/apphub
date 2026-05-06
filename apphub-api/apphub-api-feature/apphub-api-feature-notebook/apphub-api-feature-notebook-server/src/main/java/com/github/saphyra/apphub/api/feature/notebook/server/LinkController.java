package com.github.saphyra.apphub.api.feature.notebook.server;

import com.github.saphyra.apphub.api.feature.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.NotebookEndpoints;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface LinkController {
    @RequestMapping(method = RequestMethod.PUT, path = NotebookEndpoints.NOTEBOOK_CREATE_LINK)
    OneParamResponse<UUID> createLink(@RequestBody LinkRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}