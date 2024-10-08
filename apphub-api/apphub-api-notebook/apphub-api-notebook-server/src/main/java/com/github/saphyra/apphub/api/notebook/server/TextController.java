package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.NotebookEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface TextController {
    @RequestMapping(method = RequestMethod.PUT, path = NotebookEndpoints.NOTEBOOK_CREATE_TEXT)
    OneParamResponse<UUID> createText(@RequestBody CreateTextRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = NotebookEndpoints.NOTEBOOK_GET_TEXT)
    TextResponse getText(@PathVariable("listItemId") UUID textId);

    @RequestMapping(method = RequestMethod.POST, path = NotebookEndpoints.NOTEBOOK_EDIT_TEXT)
    void editText(@RequestBody EditTextRequest request, @PathVariable("listItemId") UUID textId);
}
