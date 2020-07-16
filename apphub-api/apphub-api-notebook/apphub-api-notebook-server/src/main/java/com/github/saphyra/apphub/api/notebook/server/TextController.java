package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface TextController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_TEXT)
    OneParamResponse<UUID> createText(@RequestBody CreateTextRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
