package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class GameCreationRequestValidator {
    private final AllianceNameValidator allianceNameValidator;

    void validate(SkyXploreGameCreationRequest request) {
        if (isNull(request.getHost())) {
            throw ExceptionFactory.invalidParam("host", "must not be null");
        }

        if (isNull(request.getPlayers())) {
            throw ExceptionFactory.invalidParam("members", "must not be null");
        }

        if (!request.getPlayers().containsKey(request.getHost())) {
            throw ExceptionFactory.invalidParam("host", "unknown id");
        }

        if (isNull(request.getAlliances())) {
            throw ExceptionFactory.invalidParam("alliances", "must not be null");
        }

        request.getAlliances()
            .values()
            .forEach(allianceNameValidator::validate);

        long distinctAmount = request.getAlliances()
            .values()
            .stream()
            .distinct()
            .count();
        if (distinctAmount != request.getAlliances().size()) {
            throw ExceptionFactory.invalidParam("allianceName", "not unique");
        }

        boolean allianceIdsAreKnown = request.getPlayers()
            .values()
            .stream()
            .filter(uuid -> !isNull(uuid))
            .allMatch(uuid -> request.getAlliances().containsKey(uuid));
        if (!allianceIdsAreKnown) {
            throw ExceptionFactory.invalidParam("members", "contains unknown allianceId");
        }

        if (isNull(request.getGameName())) {
            throw ExceptionFactory.invalidParam("gameName", "must not be null");
        }

        if (request.getGameName().length() < 3) {
            throw ExceptionFactory.invalidParam("gameName", "too short");
        }

        if (request.getGameName().length() > 30) {
            throw ExceptionFactory.invalidParam("gameName", "too long");
        }
    }
}
