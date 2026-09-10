package com.github.saphyra.apphub.integration.backend.admin_panel.migration_tasks;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.MigrationTasksActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MigrationTasksResponse;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Semaphore;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksTest extends BackEndTest {
    private static final String EVENT = "backend-migration-tasks-event";
    private static final String NAME = "Event for BE test";
    private static final Semaphore SEMAPHORE = new Semaphore(1);

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws InterruptedException {
        SEMAPHORE.acquire();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteEvent() {
        try {
            DatabaseUtil.deleteMigrationTaskByEvent(EVENT);
        } finally {
            SEMAPHORE.release();
        }
    }

    @Test(groups = {"be", "admin-panel"})
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

    @Test(groups = {"be", "admin-panel"})
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
