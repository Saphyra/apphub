package com.github.saphyra.apphub.api.modules.server;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient("modules")
public interface ModulesController {
    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_MODULES_OF_USER)
    Map<String, List<ModuleResponse>> getModules(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessToken);
}
