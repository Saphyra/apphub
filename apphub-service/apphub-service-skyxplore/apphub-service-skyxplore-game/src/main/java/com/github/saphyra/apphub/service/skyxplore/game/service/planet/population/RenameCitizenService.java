package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
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
    private final CitizenToModelConverter citizenToModelConverter;
    private final GameDataProxy gameDataProxy;
    private final CitizenToResponseConverter citizenToResponseConverter;

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
                gameDataProxy.saveItem(citizenToModelConverter.convert(game.getGameId(), citizen));
                return citizenToResponseConverter.convert(game.getData(), citizen);
            })
            .getOrThrow();
    }
}
