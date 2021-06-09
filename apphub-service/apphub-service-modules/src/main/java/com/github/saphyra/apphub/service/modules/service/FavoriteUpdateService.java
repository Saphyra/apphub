package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.modules.ModulesProperties;
import com.github.saphyra.apphub.service.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class FavoriteUpdateService {
    private final FavoriteService favoriteService;
    private final ModulesProperties modulesProperties;

    public void updateFavorite(UUID userId, String module, Boolean favoriteValue) {
        if (!isValidModule(module)) {
            throw ExceptionFactory.invalidParam("module", "does not exist");
        }

        if (isNull(favoriteValue)) {
            throw ExceptionFactory.invalidParam("value", "must not be null");
        }

        Favorite favorite = favoriteService.getOrDefault(userId, module);
        favorite.setFavorite(favoriteValue);
        favoriteService.save(favorite);
    }

    private boolean isValidModule(String moduleName) {
        return modulesProperties.getModules()
            .values()
            .stream()
            .flatMap(Collection::stream)
            .anyMatch(module -> module.getName().equals(moduleName));
    }
}
