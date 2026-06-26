package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation;


import com.github.saphyra.apphub.api.feature.task_manager.model.invitation.InvitationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.server.InvitationController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class InvitationControllerImpl implements InvitationController {
    private final InvitationService invitationService;

    @Override
    public List<InvitationResponse> getInvitations(AccessToken accessToken) {
        log.info("{} wants to know their invitations", accessToken.getUserId());

        return invitationService.getInvitations(accessToken.getUserId());
    }

    @Override
    public void acceptInvitation(UUID organizationId, AccessToken accessToken) {
        log.info("{} wants to accept the invitation to organization {}", accessToken.getUserId(), organizationId);

        invitationService.acceptInvitation(accessToken.getUserId(), organizationId);
    }

    @Override
    public void rejectInvitation(UUID organizationId, AccessToken accessToken) {
        log.info("{} wants to reject the invitation to organization {}", accessToken.getUserId(), organizationId);

        invitationService.rejectInvitation(accessToken.getUserId(), organizationId);
    }
}
