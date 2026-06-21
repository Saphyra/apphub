package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.invitation.InvitationResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.service.AlmService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.Invitation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.InvitationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao.InvitationFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.notification.service.NotificationService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.Organization;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationFactory invitationFactory;
    private final InvitationDao invitationDao;
    private final AccountClient accountClient;
    private final OrganizationDao organizationDao;
    private final AlmService almService;
    private final NotificationService notificationService;

    public void invite(UUID userId, UUID organizationId, List<UUID> invitedUsers) {
        List<Invitation> invitations = invitedUsers.stream()
            .map(invitedUserId -> invitationFactory.create(userId, organizationId, invitedUserId))
            .toList();

        invitationDao.save(invitations);
    }

    public List<InvitationResponse> getInvitations(UUID userId) {
        Map<UUID, AccountResponse> accounts = new HashMap<>();
        Map<UUID, Organization> organizations = new HashMap<>();

        return invitationDao.getByInvitedUserId(userId)
            .stream()
            .map(invitation -> {
                AccountResponse invitedBy = accounts.computeIfAbsent(invitation.getInvitedBy(), accountClient::getAccountInternal);
                Organization organization = organizations.computeIfAbsent(invitation.getOrganizationId(), organizationDao::findByIdValidated);
                return InvitationResponse.builder()
                    .invitedByUserId(invitation.getInvitedBy())
                    .invitedByUsername(invitedBy.getUsername())
                    .invitedByUserEmail(invitedBy.getEmail())
                    .organizationId(invitation.getOrganizationId())
                    .organizationName(organization.getName())
                    .organizationDescription(organization.getDescription())
                    .build();
            })
            .toList();
    }

    public void acceptInvitation(UUID userId, UUID organizationId) {
        List<Invitation> invitations = invitationDao.getByInvitedUserIdAndOrganizationId(userId, organizationId);

        if (invitations.isEmpty()) {
            throw ExceptionFactory.notFound("Invitation not found for organizationId " + organizationId);
        }

        almService.grantOperations(userId, PrincipalType.USER, organizationId, ObjectType.ORGANIZATION, List.of(Operation.READ));
        invitationDao.delete(invitations);

        List<UUID> invitorIds = invitations.stream()
            .map(Invitation::getInvitedBy)
            .toList();

        notificationService.createUserAcceptedYourInvitationNotification(invitorIds, userId);
    }

    public void rejectInvitation(UUID userId, UUID organizationId) {
        List<Invitation> invitations = invitationDao.getByInvitedUserIdAndOrganizationId(userId, organizationId);
        if (invitations.isEmpty()) {
            return;
        }

        List<UUID> invitorIds = invitations.stream()
            .map(Invitation::getInvitedBy)
            .toList();
        notificationService.createUserRejectedYourInvitationNotification(invitorIds, userId);

        invitationDao.delete(invitations);
    }
}
