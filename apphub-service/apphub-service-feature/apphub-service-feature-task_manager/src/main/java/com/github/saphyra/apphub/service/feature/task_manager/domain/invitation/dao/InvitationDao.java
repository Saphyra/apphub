package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InvitationDao implements DeleteByUserIdDao {
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

    @Override
    public void deleteByUserId(UUID userId) {
        List<Invitation> toDelete = Stream.concat(
                getByInvitedUserId(userId).stream(),
                getByInvitedByUserId(userId).stream()
            )
            .toList();
        repository.delete(toDelete);
    }

    private List<Invitation> getByInvitedByUserId(UUID userId) {
        return repository.getByInvitedByUserId(userId);
    }

    public void deleteByOrganizationId(UUID organizationId) {
        repository.delete(repository.getByOrganizationId(organizationId));
    }
}
