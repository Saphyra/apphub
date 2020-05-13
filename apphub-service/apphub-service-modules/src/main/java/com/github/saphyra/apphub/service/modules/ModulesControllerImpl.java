package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.api.modules.server.ModulesController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.modules.service.FavoriteUpdateService;
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
    private final FavoriteService favoriteService;
    private final FavoriteUpdateService favoriteUpdateService;
    private final ModulesQueryService modulesQueryService;

    @Override
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Processing event {}", request.getPayload());
        favoriteService.deleteByUserId(request.getPayload().getUserId());
    }

    @Override
    public Map<String, List<ModuleResponse>> getModules(AccessTokenHeader accessToken) {
        log.info("Querying available modules for user {}", accessToken.getUserId());
        Map<String, List<ModuleResponse>> result = modulesQueryService.getModules(accessToken.getUserId());
        log.info("Available modules for user {}: {}", accessToken.getUserId(), result);
        return result;
    }

    @Override
    public Map<String, List<ModuleResponse>> setFavorite(AccessTokenHeader accessToken, String module, OneParamRequest<Boolean> favorite) {
        log.info("Setting favorite status of module {} for user {}", module, accessToken.getUserId());
        favoriteUpdateService.updateFavorite(accessToken.getUserId(), module, favorite.getValue());
        return modulesQueryService.getModules(accessToken.getUserId());
    }
}
