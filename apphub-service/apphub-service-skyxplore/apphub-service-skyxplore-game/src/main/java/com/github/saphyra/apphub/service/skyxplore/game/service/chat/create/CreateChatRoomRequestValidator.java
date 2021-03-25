package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateChatRoomRequestValidator {
    void validate(CreateChatRoomRequest request, Game game) {
        if (isNull(request.getMembers())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "members", "must not be null"), "Members must not be null");
        }

        if (request.getMembers().contains(null)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "members", "must not contain null"), "Members must not contain null");
        }

        boolean allMember = request.getMembers()
            .stream()
            .allMatch(userId -> game.getPlayers().containsKey(userId));
        if (!allMember) {
            throw new ForbiddenException(new ErrorMessage(ErrorCode.FORBIDDEN_OPERATION.name()), "Cannot create chatRoom. Players are from different game.");
        }

        if (isNull(request.getRoomTitle())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "roomTitle", "must not be null"), "RoomTitle must not be null");
        }

        if (request.getRoomTitle().length() < 3) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHAT_ROOM_TITLE_TOO_SHORT.name()), "ChatRoom name too short");
        }

        if (request.getRoomTitle().length() > 20) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.CHAT_ROOM_TITLE_TOO_LONG.name()), "ChatRoom name too long");
        }
    }
}
