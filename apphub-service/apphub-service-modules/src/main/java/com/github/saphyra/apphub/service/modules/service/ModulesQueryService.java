package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.service.modules.ModulesProperties;
import com.github.saphyra.apphub.service.modules.domain.Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
public class ModulesQueryService {
    private final ModulesProperties modulesProperties;

    public Map<String, List<ModuleResponse>> getModules(UUID userId) {
        List<String> favoriteModules = new ArrayList<>(); //TODO query user's data
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
        //TODO check user's roles
        boolean result = module.isAllowedByDefault();
        log.debug("Module {} is allowed for user {}: {}", module, userId, result);
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
