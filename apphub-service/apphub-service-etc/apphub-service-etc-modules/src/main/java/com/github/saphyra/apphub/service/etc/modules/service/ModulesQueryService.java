package com.github.saphyra.apphub.service.etc.modules.service;

import com.github.saphyra.apphub.api.etc.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.etc.modules.ModulesProperties;
import com.github.saphyra.apphub.service.etc.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.etc.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.etc.modules.domain.Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
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
    private final DisabledRoleCache disabledRoleCache;

    public Map<String, List<ModuleResponse>> getModules(UUID userId, boolean mobileClient) {
        List<String> favoriteModules = favoriteService.getByUserId(userId)
            .stream()
            .filter(Favorite::isFavorite)
            .map(Favorite::getModule)
            .collect(Collectors.toList());
        Map<String, List<ModuleResponse>> result = new HashMap<>();

        for (Map.Entry<String, List<Module>> category : modulesProperties.getModules().entrySet()) {
            List<ModuleResponse> availableModules = category.getValue()
                .stream()
                .filter(module -> isAvailableForClient(module, mobileClient))
                .filter(module -> isAvailableForUser(userId, module))
                .filter(this::allRolesEnabled)
                .map(module -> convert(isFavorite(favoriteModules, module), module))
                .collect(Collectors.toList());

            if (!availableModules.isEmpty()) {
                result.put(category.getKey(), availableModules);
            }
        }
        return result;
    }

    private boolean allRolesEnabled(Module module) {
        List<Role> disabledRoles = disabledRoleCache.get();

        boolean result = disabledRoles.stream()
            .noneMatch(module.getRoles()::contains);

        if (!result) {
            log.info("Module {} is disabled. Required roles: {}, disabled roles: {}", module.getName(), module.getRoles(), disabledRoles);
        }

        return result;
    }

    private boolean isAvailableForClient(Module module, boolean mobileClient) {
        if (mobileClient) {
            boolean result = module.isMobileAllowed();
            if (!result) {
                log.info("Module {} is not allowed for mobile client", module.getName());
            }
            return result;
        }

        return true;
    }

    private boolean isAvailableForUser(UUID userId, Module module) {
        boolean result = userHasRole(module.getRoles());
        if (!result) {
            log.debug("Module {} is not allowed for user {}", module.getName(), userId);
        }
        return result;
    }

    private boolean userHasRole(List<Role> roles) {
        AccessToken accessToken = accessTokenProvider.get();
        boolean result = new HashSet<>(accessToken.getRoles())
            .containsAll(roles);
        log.debug("User {} has all the roles {}: {}. (Granted roles are: {})", accessToken.getUserId(), roles, result, accessToken.getRoles());
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
