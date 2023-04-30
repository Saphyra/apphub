package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class AllianceSetupValidator {
    void check(SkyXploreGameCreationRequest request) {
        if (request.getAis().isEmpty() && request.getMembers().size() == 1) {
            log.info("There is only one player. AI will be generated automatically.");
            return;
        }

        List<UUID> alliances = new ArrayList<>();

        request.getAis()
            .stream()
            .map(AiPlayer::getAllianceId)
            .forEach(alliances::add);

        alliances.addAll(request.getMembers().values());

        Set<UUID> processed = new HashSet<>();
        for (UUID allianceId : alliances) {
            if (isNull(allianceId)) {
                log.info("At least one player is without alliance.");
                return;
            }

            processed.add(allianceId);
        }

        if (processed.size() < 2) {
            throw ExceptionFactory.notLoggedException(HttpStatus.PRECONDITION_FAILED, ErrorCode.NOT_ENOUGH_ALLIANCES, "All players are in the same alliance.");
        }
    }
}
