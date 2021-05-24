package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class LobbyNameValidator {
    void validate(String lobbyName) {
        if (isNull(lobbyName)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "lobbyName", "must not be null"), "Lobby name is null.");
        }

        if (lobbyName.length() < 3) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "lobbyName", "too short"), "Lobby name is too short");
        }

        if (lobbyName.length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "lobbyName", "too long"), "Lobby name is too long");
        }
    }
}
