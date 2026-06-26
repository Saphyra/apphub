package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.task_manager.model.invitation.InvitationResponse;
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
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID INVITED_USER_ID_1 = UUID.randomUUID();
    private static final UUID INVITED_USER_ID_2 = UUID.randomUUID();
    private static final UUID INVITED_BY_1 = UUID.randomUUID();
    private static final UUID INVITED_BY_2 = UUID.randomUUID();

    @Mock
    private InvitationFactory invitationFactory;

    @Mock
    private InvitationDao invitationDao;

    @Mock
    private AccountClient accountClient;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private AlmService almService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InvitationService underTest;

    @Mock
    private Invitation invitation1;

    @Mock
    private Invitation invitation2;

    @Mock
    private Invitation invitation3;

    @Test
    void invite() {
        given(invitationFactory.create(USER_ID, ORGANIZATION_ID, INVITED_USER_ID_1)).willReturn(invitation1);
        given(invitationFactory.create(USER_ID, ORGANIZATION_ID, INVITED_USER_ID_2)).willReturn(invitation2);

        underTest.invite(USER_ID, ORGANIZATION_ID, List.of(INVITED_USER_ID_1, INVITED_USER_ID_2));

        then(invitationFactory).should().create(USER_ID, ORGANIZATION_ID, INVITED_USER_ID_1);
        then(invitationFactory).should().create(USER_ID, ORGANIZATION_ID, INVITED_USER_ID_2);
        then(invitationDao).should().save(List.of(invitation1, invitation2));
    }

    @Test
    void getInvitations() {
        given(invitation1.getInvitedBy()).willReturn(INVITED_BY_1);
        given(invitation1.getOrganizationId()).willReturn(ORGANIZATION_ID);
        given(invitation2.getInvitedBy()).willReturn(INVITED_BY_1);
        given(invitation2.getOrganizationId()).willReturn(ORGANIZATION_ID);
        given(invitation3.getInvitedBy()).willReturn(INVITED_BY_2);
        given(invitation3.getOrganizationId()).willReturn(ORGANIZATION_ID);
        given(invitationDao.getByInvitedUserId(USER_ID)).willReturn(List.of(invitation1, invitation2, invitation3));
        given(accountClient.getAccountInternal(INVITED_BY_1)).willReturn(
            AccountResponse.builder()
                .userId(INVITED_BY_1)
                .username("user-1")
                .email("user-1@domain.com")
                .build()
        );
        given(accountClient.getAccountInternal(INVITED_BY_2)).willReturn(
            AccountResponse.builder()
                .userId(INVITED_BY_2)
                .username("user-2")
                .email("user-2@domain.com")
                .build()
        );
        given(organizationDao.findByIdValidated(ORGANIZATION_ID)).willReturn(
            Organization.builder()
                .id(ORGANIZATION_ID)
                .name("organization-name")
                .description("organization-description")
                .build()
        );

        assertThat(underTest.getInvitations(USER_ID)).containsExactly(
            InvitationResponse.builder()
                .invitedByUserId(INVITED_BY_1)
                .invitedByUsername("user-1")
                .invitedByUserEmail("user-1@domain.com")
                .organizationId(ORGANIZATION_ID)
                .organizationName("organization-name")
                .organizationDescription("organization-description")
                .build(),
            InvitationResponse.builder()
                .invitedByUserId(INVITED_BY_1)
                .invitedByUsername("user-1")
                .invitedByUserEmail("user-1@domain.com")
                .organizationId(ORGANIZATION_ID)
                .organizationName("organization-name")
                .organizationDescription("organization-description")
                .build(),
            InvitationResponse.builder()
                .invitedByUserId(INVITED_BY_2)
                .invitedByUsername("user-2")
                .invitedByUserEmail("user-2@domain.com")
                .organizationId(ORGANIZATION_ID)
                .organizationName("organization-name")
                .organizationDescription("organization-description")
                .build()
        );

        then(accountClient).should(times(1)).getAccountInternal(INVITED_BY_1);
        then(accountClient).should(times(1)).getAccountInternal(INVITED_BY_2);
        then(organizationDao).should(times(1)).findByIdValidated(ORGANIZATION_ID);
    }

    @Test
    void acceptInvitation() {
        given(invitation1.getInvitedBy()).willReturn(INVITED_BY_1);
        given(invitation2.getInvitedBy()).willReturn(INVITED_BY_2);
        given(invitationDao.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).willReturn(List.of(invitation1, invitation2));

        underTest.acceptInvitation(USER_ID, ORGANIZATION_ID);

        then(almService).should().grantOperations(USER_ID, PrincipalType.USER, ORGANIZATION_ID, ObjectType.ORGANIZATION, List.of(Operation.READ));
        then(invitationDao).should().delete(List.of(invitation1, invitation2));
        then(notificationService).should().createUserAcceptedYourInvitationNotification(List.of(INVITED_BY_1, INVITED_BY_2), ORGANIZATION_ID, USER_ID);
    }

    @Test
    void acceptInvitation_notFound() {
        given(invitationDao.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).willReturn(List.of());

        ExceptionValidator.validateNotFoundException(() -> underTest.acceptInvitation(USER_ID, ORGANIZATION_ID));

        then(almService).shouldHaveNoInteractions();
        then(notificationService).shouldHaveNoInteractions();
    }

    @Test
    void rejectInvitation() {
        given(invitation1.getInvitedBy()).willReturn(INVITED_BY_1);
        given(invitation2.getInvitedBy()).willReturn(INVITED_BY_2);
        given(invitationDao.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).willReturn(List.of(invitation1, invitation2));

        underTest.rejectInvitation(USER_ID, ORGANIZATION_ID);

        then(notificationService).should().createUserRejectedYourInvitationNotification(List.of(INVITED_BY_1, INVITED_BY_2), ORGANIZATION_ID, USER_ID);
        then(invitationDao).should().delete(List.of(invitation1, invitation2));
    }

    @Test
    void rejectInvitation_notFound() {
        given(invitationDao.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).willReturn(List.of());

        underTest.rejectInvitation(USER_ID, ORGANIZATION_ID);

        then(notificationService).shouldHaveNoInteractions();
        then(invitationDao).should(times(0)).delete(List.of());
    }

}