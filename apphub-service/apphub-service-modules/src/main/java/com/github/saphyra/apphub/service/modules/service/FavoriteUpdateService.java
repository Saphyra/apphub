package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
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
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "module", "does not exist"), String.format("Module does not exist with name %s", module));
        }

        if (isNull(favoriteValue)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "must not be null"), "favoriteValue is null.");
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
