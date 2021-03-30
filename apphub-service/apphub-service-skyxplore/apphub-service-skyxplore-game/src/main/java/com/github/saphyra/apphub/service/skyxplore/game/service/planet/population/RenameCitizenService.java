package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
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

    void renameCitizen(UUID userId, UUID planetId, UUID citizenId, String newName) {
        if (isBlank(newName)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "must not be blank"), "Citizen name is blank");
        }

        if (newName.length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "too long"), "Citizen name is too long");
        }

        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPopulation()
            .getOptional(citizenId)
            .orElseThrow(() -> RestException.createNonTranslated(HttpStatus.NOT_FOUND, "Citizen not found with id " + citizenId))
            .setName(newName);
    }
}
