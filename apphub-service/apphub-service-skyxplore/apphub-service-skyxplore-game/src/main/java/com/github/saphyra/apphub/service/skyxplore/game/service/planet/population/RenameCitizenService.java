package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class RenameCitizenService {
    private final GameDao gameDao;
    private final CitizenToModelConverter citizenToModelConverter;
    private final GameDataProxy gameDataProxy;

    void renameCitizen(UUID userId, UUID planetId, UUID citizenId, String newName) {
        if (isBlank(newName)) {
            throw ExceptionFactory.invalidParam("value", "must not be null or blank");
        }

        if (newName.length() > 30) {
            throw ExceptionFactory.invalidParam("value", "too long");
        }

        Game game = gameDao.findByUserIdValidated(userId);
        Citizen citizen = game.getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId)
            .getPopulation()
            .getOptional(citizenId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "Citizen not found with id " + citizenId));
        citizen.setName(newName);
        gameDataProxy.saveItem(citizenToModelConverter.convert(citizen, game.getGameId()));
    }
}
