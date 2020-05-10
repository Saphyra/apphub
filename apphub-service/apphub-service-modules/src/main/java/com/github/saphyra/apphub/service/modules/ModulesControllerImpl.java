package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.api.modules.server.ModulesController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.modules.service.ModulesQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO api test
//TODO int test
//TODO fe test
public class ModulesControllerImpl implements ModulesController {
    private final ModulesQueryService modulesQueryService;

    @Override
    public Map<String, List<ModuleResponse>> getModules(AccessTokenHeader accessToken) {
        log.info("Querying available modules for user {}", accessToken.getUserId());
        Map<String, List<ModuleResponse>> result = modulesQueryService.getModules(accessToken.getUserId());
        log.info("Available modules for user {}: {}", accessToken.getUserId(), result);
        return result;
    }
}
