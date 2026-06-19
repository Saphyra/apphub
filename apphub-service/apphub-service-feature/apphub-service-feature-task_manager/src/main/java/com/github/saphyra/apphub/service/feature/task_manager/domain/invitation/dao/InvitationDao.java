package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class InvitationDao {
    private final List<Invitation> repository = Collections.synchronizedList(new ArrayList<>());

    public void save(List<Invitation> invitations) {
        repository.addAll(invitations);
    }

    public List<Invitation> getByInvitedUserId(UUID invitedUserId) {
        return repository.stream()
            .filter(invitation -> invitation.getInvitedUserId().equals(invitedUserId))
            .toList();
    }

    public List<Invitation> getByInvitedUserIdAndOrganizationId(UUID userId, UUID organizationId) {
        return repository.stream()
            .filter(invitation -> invitation.getInvitedUserId().equals(userId))
            .filter(invitation -> invitation.getOrganizationId().equals(organizationId))
            .toList();
    }

    public void delete(List<Invitation> invitations) {
        invitations.forEach(repository::remove);
    }
}
