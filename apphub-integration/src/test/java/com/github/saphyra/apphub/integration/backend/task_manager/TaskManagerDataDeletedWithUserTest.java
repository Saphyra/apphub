package com.github.saphyra.apphub.integration.backend.task_manager;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerInvitationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerOrganizationActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager.TaskManagerAlmDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager.TaskManagerInvitationDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager.TaskManagerNotificationDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager.TaskManagerOrganizationDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerDataDeletedWithUserTest extends BackEndTest {
    private static final String ORGANIZATION_NAME = "organization-name";
    private static final String DESCRIPTION = "description";

    @Test(groups = {"be", "task-manager"})
    public void dataDeletedWithUser() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = UserDynamoDbRepository.getUserIdByEmail(userData3.getEmail());

        UUID organizationId = createOrganization(accessToken1, userId2, userId3);
        acceptInvitation(accessToken2, organizationId);

        AccountActions.deleteAccount(getServerPort(), accessToken1, userData1.getPassword());

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(TaskManagerOrganizationDynamoDbRepository.findOrganization(organizationId)).isEmpty();
            assertThat(TaskManagerAlmDynamoDbRepository.getAlmsByObject(organizationId, "ORGANIZATION")).isEmpty();
            assertThat(TaskManagerInvitationDynamoDbRepository.getInvitationsByUserId(userId3)).isEmpty();
            assertThat(TaskManagerNotificationDynamoDbRepository.getNotificationsByUserId(userId1)).isEmpty();
        });
    }

    private void acceptInvitation(String accessToken2, UUID organizationId) {
        TaskManagerInvitationActions.acceptInvitation(getServerPort(), accessToken2, organizationId);
    }

    private UUID createOrganization(String accessToken1, UUID userId2, UUID userId3) {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
            .organizationName(ORGANIZATION_NAME)
            .description(DESCRIPTION)
            .invitedUsers(List.of(userId2, userId3))
            .build();

        TaskManagerOrganizationActions.createOrganization(getServerPort(), accessToken1, request);

        return TaskManagerOrganizationActions.getOrganizations(getServerPort(), accessToken1)
            .getFirst()
            .getOrganizationId();
    }
}
