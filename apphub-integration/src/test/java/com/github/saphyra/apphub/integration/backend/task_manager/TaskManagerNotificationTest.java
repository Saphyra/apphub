package com.github.saphyra.apphub.integration.backend.task_manager;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerInvitationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerNotificationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerOrganizationActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationResponse;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationStatus;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.SetNotificationStatusRequest;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerNotificationTest extends BackEndTest {
    private static final String ORGANIZATION_NAME = "organization-name";
    private static final String DESCRIPTION = "description";

    @Test(groups = {"be", "task-manager"})
    public void notification() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        UUID organizationId = createOrganization(accessToken1, userId2);
        TaskManagerInvitationActions.acceptInvitation(getServerPort(), accessToken2, organizationId);
        UUID notificationId = getNotificationId(accessToken1, organizationId);

        setStatus_nullStatus(accessToken1, notificationId);
        setStatus_nullNotificationIds(accessToken1);
        setStatus_nullInNotificationIds(accessToken1, notificationId);
        setStatus(accessToken1, organizationId, notificationId);

        deleteNotification(accessToken1, organizationId, notificationId);
    }

    private void deleteNotification(String accessToken1, UUID organizationId, UUID notificationId) {
        TaskManagerNotificationActions.deleteNotification(getServerPort(), accessToken1, Set.of(notificationId));

        assertThat(TaskManagerNotificationActions.getNotifications(getServerPort(), accessToken1, organizationId)).isEmpty();
    }

    private void setStatus(String accessToken1, UUID organizationId, UUID notificationId) {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(Set.of(notificationId))
            .build();

        TaskManagerNotificationActions.setNotificationStatusResponse(getServerPort(), accessToken1, request);

        CustomAssertions.singleListAssertThat(TaskManagerNotificationActions.getNotifications(getServerPort(), accessToken1, organizationId))
            .returns(NotificationStatus.READ, NotificationResponse::getStatus);
    }

    private void setStatus_nullInNotificationIds(String accessToken1, UUID notificationId) {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(CollectionUtils.toSet(notificationId, null))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerNotificationActions.setNotificationStatusResponse(getServerPort(), accessToken1, request), "notificationIds", "must not contain null values");
    }

    private void setStatus_nullNotificationIds(String accessToken1) {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(NotificationStatus.READ)
            .notificationIds(null)
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerNotificationActions.setNotificationStatusResponse(getServerPort(), accessToken1, request), "notificationIds", "must not be null");
    }

    private void setStatus_nullStatus(String accessToken1, UUID notificationId) {
        SetNotificationStatusRequest request = SetNotificationStatusRequest.builder()
            .status(null)
            .notificationIds(Set.of(notificationId))
            .build();

        ResponseValidator.verifyInvalidParam(TaskManagerNotificationActions.setNotificationStatusResponse(getServerPort(), accessToken1, request), "status", "must not be null");
    }

    private UUID getNotificationId(String accessToken1, UUID organizationId) {
        return TaskManagerNotificationActions.getNotifications(getServerPort(), accessToken1, organizationId)
            .getFirst()
            .getNotificationId();
    }

    private UUID createOrganization(String accessToken1, UUID userId2) {
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
