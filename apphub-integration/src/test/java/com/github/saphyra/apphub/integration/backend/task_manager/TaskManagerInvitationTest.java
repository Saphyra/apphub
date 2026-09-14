package com.github.saphyra.apphub.integration.backend.task_manager;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerInvitationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerNotificationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerOrganizationActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.task_manager.invitation.InvitationResponse;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationResponse;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationStatus;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationType;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.OrganizationResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerInvitationTest extends BackEndTest {
    private static final String ORGANIZATION_NAME = "organization-name";
    private static final String DESCRIPTION = "description";

    @Test(groups = {"be", "task-manager"})
    void acceptInvitation() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        UUID organizationId = createOrganization(userId2, accessToken1);

        getInvitations(accessToken2, userId1, userData1, organizationId);
        accept_notFound(accessToken2);
        accept(accessToken2, organizationId);

        assertThat(TaskManagerNotificationActions.getNotifications(getServerPort(), accessToken1, organizationId))
            .singleElement()
            .returns(NotificationStatus.UNREAD, NotificationResponse::getStatus)
            .returns(NotificationType.USER_ACCEPTED_YOUR_INVITATION, NotificationResponse::getType)
            .returns(
                Map.of(
                    "username", userData2.getUsername(),
                    "email", userData2.getEmail()
                ),
                NotificationResponse::getData
            );
    }

    @Test(groups = {"be", "task-manager"})
    void rejectInvitation() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        UUID organizationId = createOrganization(userId2, accessToken1);

        getInvitations(accessToken2, userId1, userData1, organizationId);
        reject(accessToken2, organizationId);
    }

    private void reject(String accessToken2, UUID organizationId) {
        TaskManagerInvitationActions.rejectInvitation(getServerPort(), accessToken2, organizationId);

        assertThat(TaskManagerOrganizationActions.getOrganizations(getServerPort(), accessToken2)).isEmpty();
    }

    private void accept(String accessToken2, UUID organizationId) {
        TaskManagerInvitationActions.acceptInvitation(getServerPort(), accessToken2, organizationId);

        assertThat(TaskManagerOrganizationActions.getOrganizations(getServerPort(), accessToken2))
            .extracting(OrganizationResponse::getOrganizationId)
            .containsExactly(organizationId);
    }

    private void accept_notFound(String accessToken2) {
        ResponseValidator.verifyErrorResponse(TaskManagerInvitationActions.acceptInvitationResponse(getServerPort(), accessToken2, UUID.randomUUID()), 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void getInvitations(String accessToken2, UUID userId1, RegistrationParameters userData1, UUID organizationId) {
        InvitationResponse invitation = TaskManagerInvitationActions.getInvitations(getServerPort(), accessToken2)
            .getFirst();
        assertThat(invitation)
            .returns(userId1, InvitationResponse::getInvitedByUserId)
            .returns(userData1.getUsername(), InvitationResponse::getInvitedByUsername)
            .returns(userData1.getEmail(), InvitationResponse::getInvitedByUserEmail)
            .returns(organizationId, InvitationResponse::getOrganizationId)
            .returns(ORGANIZATION_NAME, InvitationResponse::getOrganizationName)
            .returns(DESCRIPTION, InvitationResponse::getOrganizationDescription);
    }

    private static UUID createOrganization(UUID userId2, String accessToken1) {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
            .organizationName(ORGANIZATION_NAME)
            .description(DESCRIPTION)
            .invitedUsers(List.of(userId2))
            .build();
        TaskManagerOrganizationActions.createOrganization(getServerPort(), accessToken1, request);

        return TaskManagerOrganizationActions.getOrganizations(getServerPort(), accessToken1)
            .getFirst()
            .getOrganizationId();
    }
}
