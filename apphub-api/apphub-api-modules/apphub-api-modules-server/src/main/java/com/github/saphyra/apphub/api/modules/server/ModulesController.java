package com.github.saphyra.apphub.api.modules.server;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("modules")
public interface ModulesController {
    @RequestMapping(method = RequestMethod.POST, path = GenericEndpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);

    @RequestMapping(method = RequestMethod.GET, path = ModulesEndpoints.MODULES_GET_MODULES_OF_USER)
    Map<String, List<ModuleResponse>> getModules(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken, @RequestParam(name = "mobile", required = false, defaultValue = "false") boolean mobileClient);

    @RequestMapping(method = RequestMethod.POST, path = ModulesEndpoints.MODULES_SET_FAVORITE)
    Map<String, List<ModuleResponse>> setFavorite(
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken,
        @PathVariable("module") String module,
        @RequestBody OneParamRequest<Boolean> favorite
    );
}
