package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class InvitationFactory {
    private final DateTimeUtil dateTimeUtil;

    Invitation create(UUID characterId) {
        return Invitation.builder()
            .characterId(characterId)
            .invitationTime(dateTimeUtil.getCurrentDate())
            .build();
    }
}
