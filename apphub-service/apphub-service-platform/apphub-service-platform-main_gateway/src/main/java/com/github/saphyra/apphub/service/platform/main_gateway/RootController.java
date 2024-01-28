package com.github.saphyra.apphub.service.platform.main_gateway;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RootController {
    private final ErrorReporterService errorReporterService;
    private final AccessTokenQueryService accessTokenQueryService;

    @GetMapping("/api")
    @ResponseBody
    public void availabilityCheck() {
        log.info("Availability check was executed by a mobile device.");
    }


    @GetMapping("/")
    public String rootMapping() {
        log.info("Root was called. Redirecting to index page.");
        return String.format("redirect:%s", Endpoints.INDEX_PAGE);
    }

    @GetMapping("/report-error")
    public String reportError() {
        log.info("Error report endpoint was called.");
        errorReporterService.report("Test-error", new RuntimeException("Test exception"));
        return rootMapping();
    }

    @GetMapping(Endpoints.GET_OWN_USER_ID)
    ResponseEntity<OneParamResponse<UUID>> getOwnUserId(@CookieValue(value = Constants.ACCESS_TOKEN_COOKIE, required = false) String accessTokenId) {
        return accessTokenQueryService.getAccessToken(accessTokenId)
            .map(InternalAccessTokenResponse::getUserId)
            .map(OneParamResponse::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> new ResponseEntity<>(new OneParamResponse<>(null), HttpStatus.UNAUTHORIZED));
    }
}
