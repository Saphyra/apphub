package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.service.AlmService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service.InvitationService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.Organization;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationServiceTest {
    private static final String ORGANIZATION_NAME = "organization-name";
    private static final String DESCRIPTION = "description";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID INVITED_USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private OrganizationFactory organizationFactory;

    @Mock
    private AccountClient accountClient;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private AlmService almService;

    @Mock
    private InvitationService invitationService;

    @InjectMocks
    private CreateOrganizationService underTest;

    @Mock
    private Organization organization;

    @Test
    void blankOrganizationName() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .organizationName(" ")
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "organizationName", "must not be null or blank");
    }

    @Test
    void tooLongOrganizationName() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .organizationName("a".repeat(TaskManagerConstants.MAX_ORGANIZATION_NAME_LENGTH + 1))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "organizationName", "too long");
    }

    @Test
    void nullDescription() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .description(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "description", "must not be null");
    }

    @Test
    void tooLongDescription() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .description("a".repeat(TaskManagerConstants.MAX_ORGANIZATION_DESCRIPTION_LENGTH + 1))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "description", "too long");
    }

    @Test
    void nullInvitedUsers() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "invitedUsers", "must not be null");
    }

    @Test
    void invitedUsersContainsNull() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(CollectionUtils.toList(INVITED_USER_ID, null))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "invitedUsers", "must not contain null values");
    }

    @Test
    void invitedUserDoesNotExist() {
        CreateOrganizationRequest request = validRequest();

        given(accountClient.userExists(INVITED_USER_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "invitedUser", "does not exist");
    }

    @Test
    void selfInvitation() {
        CreateOrganizationRequest request = validRequest()
            .toBuilder()
            .invitedUsers(List.of(USER_ID))
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.createOrganization(USER_ID, request), "invitedUser", "cannot invite yourself");
    }

    @Test
    void createOrganization() {
        CreateOrganizationRequest request = validRequest();

        given(accountClient.userExists(INVITED_USER_ID)).willReturn(true);
        given(organizationFactory.create(ORGANIZATION_NAME, DESCRIPTION)).willReturn(organization);
        given(organization.getId()).willReturn(ORGANIZATION_ID);

        underTest.createOrganization(USER_ID, request);

        then(organizationDao).should().save(organization);
        then(almService).should().grantOperations(USER_ID, PrincipalType.USER, ORGANIZATION_ID, ObjectType.ORGANIZATION, List.of(Operation.OWNER));
        then(invitationService).should().invite(USER_ID, ORGANIZATION_ID, List.of(INVITED_USER_ID));
    }

    private CreateOrganizationRequest validRequest() {
        return CreateOrganizationRequest.builder()
            .organizationName(ORGANIZATION_NAME)
            .description(DESCRIPTION)
            .invitedUsers(List.of(INVITED_USER_ID))
            .build();
    }
}