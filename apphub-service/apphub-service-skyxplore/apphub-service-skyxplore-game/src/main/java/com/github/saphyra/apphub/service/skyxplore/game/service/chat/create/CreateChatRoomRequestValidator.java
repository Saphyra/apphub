package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateChatRoomRequestValidator {
    void validate(CreateChatRoomRequest request, Game game) {
        if (isNull(request.getMembers())) {
            throw ExceptionFactory.invalidParam("members", "must not be null");
        }

        if (request.getMembers().contains(null)) {
            throw ExceptionFactory.invalidParam("members", "must not contain null");
        }

        boolean allMember = request.getMembers()
            .stream()
            .allMatch(userId -> game.getPlayers().containsKey(userId));
        if (!allMember) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, "Cannot create chatRoom. Players are from different game.");
        }

        if (isNull(request.getRoomTitle())) {
            throw ExceptionFactory.invalidParam("roomTitle", "must not be null");
        }

        if (request.getRoomTitle().length() < 3) {
            throw ExceptionFactory.invalidParam("roomTitle", "too short");
        }

        if (request.getRoomTitle().length() > 20) {
            throw ExceptionFactory.invalidParam("roomTitle", "too long");
        }
    }
}
