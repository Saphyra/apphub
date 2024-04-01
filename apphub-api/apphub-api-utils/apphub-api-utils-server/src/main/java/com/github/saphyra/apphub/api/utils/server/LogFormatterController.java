package com.github.saphyra.apphub.api.utils.server;

import com.github.saphyra.apphub.api.utils.model.log_formatter.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.api.utils.model.log_formatter.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface LogFormatterController {
    /**
     * Queries visibility for the given list of params. If the param is not already present in the database, it will be created, and marked as "visible" by default.
     *
     * @param parameters List of log message keys to search for
     */
    @PutMapping(Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY)
    List<LogParameterVisibilityResponse> getVisibility(@RequestBody List<String> parameters, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Sets the visibility for the given parameter identified by its UUID.
     */
    @PostMapping(Endpoints.UTILS_LOG_FORMATTER_SET_VISIBILITY)
    void setVisibility(@RequestBody SetLogParameterVisibilityRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
