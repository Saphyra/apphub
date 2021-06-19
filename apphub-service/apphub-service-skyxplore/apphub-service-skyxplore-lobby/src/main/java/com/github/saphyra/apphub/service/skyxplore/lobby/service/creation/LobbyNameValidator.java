package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
            throw ExceptionFactory.invalidParam("lobbyName", "must not be null");
        }

        if (lobbyName.length() < 3) {
            throw ExceptionFactory.invalidParam("lobbyName", "too short");
        }

        if (lobbyName.length() > 30) {
            throw ExceptionFactory.invalidParam("lobbyName", "too long");
        }
    }
}
