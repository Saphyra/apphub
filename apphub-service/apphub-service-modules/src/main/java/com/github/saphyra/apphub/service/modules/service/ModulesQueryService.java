package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.modules.ModulesProperties;
import com.github.saphyra.apphub.service.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.modules.domain.Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class ModulesQueryService {
    private final AccessTokenProvider accessTokenProvider;
    private final FavoriteService favoriteService;
    private final ModulesProperties modulesProperties;

    public Map<String, List<ModuleResponse>> getModules(UUID userId) {
        List<String> favoriteModules = favoriteService.getByUserId(userId)
            .stream()
            .filter(Favorite::isFavorite)
            .map(Favorite::getModule)
            .collect(Collectors.toList());
        Map<String, List<ModuleResponse>> result = new HashMap<>();

        for (Map.Entry<String, List<Module>> category : modulesProperties.getModules().entrySet()) {
            List<ModuleResponse> availableModules = category.getValue()
                .stream()
                .filter(module -> isAvailable(userId, module))
                .map(module -> convert(isFavorite(favoriteModules, module), module))
                .collect(Collectors.toList());

            if (!availableModules.isEmpty()) {
                result.put(category.getKey(), availableModules);
            }
        }
        return result;
    }

    private boolean isAvailable(UUID userId, Module module) {
        boolean result = module.isAllowedByDefault() || userHasRole(module.getRole());
        log.debug("Module {} is allowed for user {}: {}", module, userId, result);
        return result;
    }

    private boolean userHasRole(String role) {
        AccessTokenHeader accessTokenHeader = accessTokenProvider.get();
        boolean result = accessTokenHeader.getRoles().contains(role);
        log.info("User {} has role {}: {}. (Granted roles are: {})", accessTokenHeader.getUserId(), role, result, accessTokenHeader.getRoles());
        return result;
    }

    private boolean isFavorite(List<String> favoriteModules, Module module) {
        boolean result = favoriteModules.contains(module.getName());
        log.debug("Module {} is favorite: {}. FavoriteModules are {}", module.getName(), result, favoriteModules);
        return result;
    }

    private ModuleResponse convert(boolean favorite, Module module) {
        return ModuleResponse.builder()
            .name(module.getName())
            .url(module.getUrl())
            .favorite(favorite)
            .build();
    }
}