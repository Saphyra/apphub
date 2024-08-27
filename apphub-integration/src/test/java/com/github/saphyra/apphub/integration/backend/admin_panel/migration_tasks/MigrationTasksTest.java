package com.github.saphyra.apphub.integration.backend.admin_panel.migration_tasks;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.MigrationTasksActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MigrationTasksResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksTest extends BackEndTest {
    private static final String EVENT = "backend-migration-tasks-event";
    private static final String NAME = "Event for BE test";

    @AfterMethod(alwaysRun = true)
    public void deleteEvent() {
        DatabaseUtil.deleteMigrationTaskByEvent(EVENT);
    }

    @Test(groups = {"be", "admin-panel"})
    public void migrationTasksTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        DatabaseUtil.insertMigrationTask(EVENT, NAME, false);

        MigrationTasksResponse task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessTokenId, EVENT);
        assertThat(task.getCompleted()).isFalse();

        MigrationTasksActions.triggerTask(getServerPort(), accessTokenId, EVENT);
        task = MigrationTasksActions.findMigrationTaskByEventValidated(getServerPort(), accessTokenId, EVENT);
        assertThat(task.getCompleted()).isTrue();

        Response response = MigrationTasksActions.getTriggerTaskResponse(getServerPort(), accessTokenId, EVENT);
        assertThat(response.getStatusCode()).isEqualTo(410);

        MigrationTasksActions.deleteTask(getServerPort(), accessTokenId, EVENT);

        assertThat(MigrationTasksActions.findMigrationTaskByEvent(getServerPort(), accessTokenId, EVENT)).isEmpty();
    }
}
