package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface TextController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_TEXT)
    OneParamResponse<UUID> createText(@RequestBody CreateTextRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_NOTEBOOK_TEXT)
    TextResponse getText(@PathVariable("textId") UUID textId);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.EDIT_NOTEBOOK_TEXT)
    void editText(@RequestBody EditTextRequest request, @PathVariable("textId") UUID textId);
}
