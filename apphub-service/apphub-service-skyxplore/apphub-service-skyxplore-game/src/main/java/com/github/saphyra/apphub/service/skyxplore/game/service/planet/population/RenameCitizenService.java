package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class RenameCitizenService {
    private final GameDao gameDao;
    private final CitizenConverter citizenConverter;
    private final GameDataProxy gameDataProxy;

    CitizenResponse renameCitizen(UUID userId, UUID citizenId, String newName) {
        if (isBlank(newName)) {
            throw ExceptionFactory.invalidParam("value", "must not be null or blank");
        }

        if (newName.length() > 30) {
            throw ExceptionFactory.invalidParam("value", "too long");
        }

        Game game = gameDao.findByUserIdValidated(userId);
        Citizen citizen = game.getData()
            .getCitizens()
            .findByCitizenIdValidated(citizenId);

        return game.getEventLoop()
            .processWithResponseAndWait(() -> {
                citizen.setName(newName);
                gameDataProxy.saveItem(citizenConverter.toModel(game.getGameId(), citizen));
                return citizenConverter.toResponse(game.getData(), citizen); //TODO do not save directly
            })
            .getOrThrow();
    }
}
