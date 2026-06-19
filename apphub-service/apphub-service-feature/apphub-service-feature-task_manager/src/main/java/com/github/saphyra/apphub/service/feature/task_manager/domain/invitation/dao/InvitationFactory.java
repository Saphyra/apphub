package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class InvitationFactory {
    public Invitation create(UUID userId, UUID organizationId, UUID invitedUserId) {
        return Invitation.builder()
            .invitedUserId(invitedUserId)
            .organizationId(organizationId)
            .invitedBy(userId)
            .build();
    }
}
