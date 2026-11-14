package com.github.saphyra.apphub.integration.backend.admin_panel.migration_tasks;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.MigrationTasksActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MigrationTasksResponse;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksTest extends BackEndTest {
    private static final String EVENT = "backend-migration-tasks-event";
    private static final String NAME = "Event for BE test";


    @AfterMethod(alwaysRun = true)
    public void deleteEvent() {
        DatabaseUtil.deleteMigrationTaskByEvent(EVENT);
    }

    /**
     * Tests migration task lifecycle.
     * <ul>
     *   <li>Creates a migration task, verifies it is incomplete.</li>
     *   <li>Triggers the task and checks completion status.</li>
     *   <li>Ensures subsequent trigger returns 410 (already triggered).</li>
     *   <li>Deletes the task and confirms removal.</li>
     * </ul>
     */
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.MIGRATION_TASK)
    public void migrationTasksTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        DatabaseUtil.insertMigrationTask(EVENT, NAME, false, false);

        MigrationTasksResponse task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessToken, EVENT);
        assertThat(task.getCompleted()).isFalse();

        MigrationTasksActions.triggerTask(getServerPort(), accessToken, EVENT);
        task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessToken, EVENT);
        assertThat(task.getCompleted()).isTrue();

        Response response = MigrationTasksActions.getTriggerTaskResponse(getServerPort(), accessToken, EVENT);
        assertThat(response.getStatusCode()).isEqualTo(410);

        MigrationTasksActions.deleteTask(getServerPort(), accessToken, EVENT);

        assertThat(MigrationTasksActions.findMigrationTaskByEvent(getServerPort(), accessToken, EVENT)).isEmpty();
    }

    /**
     * Tests repeatable migration task execution.
     * <ul>
     *   <li>Creates a repeatable migration task and verifies it is incomplete.</li>
     *   <li>Triggers the task, checks completion status, then triggers again to confirm idempotency.</li>
     *   <li>Deletes the task and confirms removal.</li>
     * </ul>
     */
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.MIGRATION_TASK)
    public void repeatableMigrationTasksTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        DatabaseUtil.insertMigrationTask(EVENT, NAME, false, true);

        MigrationTasksResponse task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessToken, EVENT);
        assertThat(task.getCompleted()).isFalse();

        MigrationTasksActions.triggerTask(getServerPort(), accessToken, EVENT);
        task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessToken, EVENT);
        assertThat(task.getCompleted()).isTrue();

        MigrationTasksActions.triggerTask(getServerPort(), accessToken, EVENT);

        MigrationTasksActions.deleteTask(getServerPort(), accessToken, EVENT);

        assertThat(MigrationTasksActions.findMigrationTaskByEvent(getServerPort(), accessToken, EVENT)).isEmpty();
    }
}
