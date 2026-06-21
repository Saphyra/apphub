package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvitationDao {
    private final InvitationRepository repository;

    public void save(List<Invitation> invitations) {
        repository.saveAll(invitations);
    }

    public List<Invitation> getByInvitedUserId(UUID invitedUserId) {
        return repository.getByUserId(invitedUserId);
    }

    public List<Invitation> getByInvitedUserIdAndOrganizationId(UUID userId, UUID organizationId) {
        return repository.getByInvitedUserIdAndOrganizationId(userId, organizationId);
    }

    public void delete(List<Invitation> invitations) {
        repository.delete(invitations);
    }
}
