package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.service.AlmService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service.InvitationService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.Organization;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CreateOrganizationService {
    private final OrganizationFactory organizationFactory;
    private final AccountClient accountClient;
    private final OrganizationDao organizationDao;
    private final AlmService almService;
    private final InvitationService invitationService;

    public void createOrganization(UUID userId, CreateOrganizationRequest request) {
        validateRequest(request);

        Organization organization = organizationFactory.create(request.getOrganizationName(), request.getDescription());
        organizationDao.save(organization);

        almService.grantOperations(userId, PrincipalType.USER, organization.getId(), ObjectType.ORGANIZATION, List.of(Operation.OWNER));
        invitationService.invite(userId, organization.getId(), request.getInvitedUsers());
    }

    private void validateRequest(CreateOrganizationRequest request) {
        ValidationUtil.notBlank(request.getOrganizationName(), "organizationName");
        ValidationUtil.notNull(request.getDescription(), "description");
        ValidationUtil.notNull(request.getInvitedUsers(), "invitedUsers");

        if (request.getInvitedUsers().stream().anyMatch(userId -> !accountClient.userExists(userId))) {
            throw ExceptionFactory.invalidParam("invitedUser", "does not exist");
        }
    }
}
