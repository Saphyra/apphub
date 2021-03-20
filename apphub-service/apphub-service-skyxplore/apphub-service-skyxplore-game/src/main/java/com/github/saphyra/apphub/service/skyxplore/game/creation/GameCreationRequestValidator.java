package com.github.saphyra.apphub.service.skyxplore.game.creation;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class GameCreationRequestValidator {
    private final GameCreationSettingsValidator gameCreationSettingsValidator;
    private final AllianceNameValidator allianceNameValidator;

    void validate(SkyXploreGameCreationRequest request) {
        gameCreationSettingsValidator.validate(request.getSettings());

        if (isNull(request.getHost())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "host", "must not be null"), "Host must not be null");
        }

        if (isNull(request.getMembers())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "members", "must not be null"), "Members must not be null");
        }

        if (!request.getMembers().containsKey(request.getHost())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "host", "unknown id"), "Host is unknown id");
        }

        if (isNull(request.getAlliances())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "alliances", "must not be null"), "Alliances must not be null");
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
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "allianceName", "not unique"), "Duplication in alliance names");
        }

        boolean allianceIdsAreKnown = request.getMembers()
            .values()
            .stream()
            .filter(uuid -> !isNull(uuid))
            .allMatch(uuid -> request.getAlliances().containsKey(uuid));
        if (!allianceIdsAreKnown) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "members", "contains unknown allianceId"), "Members contains unknown allianceId");
        }

        if (isNull(request.getGameName())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "gameName", "must not be null"), "GameName must not be null");
        }

        if (request.getGameName().length() < 3) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.GAME_NAME_TOO_SHORT.name()), "GameName must too short");
        }

        if (request.getGameName().length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.GAME_NAME_TOO_LONG.name()), "GameName must too long");
        }
    }
}
