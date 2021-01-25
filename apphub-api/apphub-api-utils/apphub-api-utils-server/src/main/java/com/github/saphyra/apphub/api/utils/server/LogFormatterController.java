package com.github.saphyra.apphub.api.utils.server;

import com.github.saphyra.apphub.api.utils.model.request.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.api.utils.model.response.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface LogFormatterController {
    @PutMapping(Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY)
    List<LogParameterVisibilityResponse> getVisibility(@RequestBody List<String> parameters, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.UTILS_LOG_FORMATTER_SET_VISIBILITY)
    void setVisibility(@RequestBody SetLogParameterVisibilityRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
