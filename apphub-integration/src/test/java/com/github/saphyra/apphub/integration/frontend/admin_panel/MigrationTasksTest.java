package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.MigrationTasksActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.admin_panel.MigrationTask;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTasksTest extends SeleniumTest {
    private static final String EVENT = "frontend-migration-tasks-event";
    private static final String NAME = "Event for BE test";

    @AfterMethod(alwaysRun = true)
    public void deleteEvent() {
        DatabaseUtil.deleteMigrationTaskByEvent(EVENT);
    }

    /**
     * Tests creating, triggering, and deleting a migration task.
     *
     * <ul>
     *   <li>Create a migration task with a unique event name.</li>
     *   <li>Verify the task is initially incomplete.</li>
     *   <li>Trigger the task and wait for completion.</li>
     *   <li>Delete the task and confirm it no longer exists.</li>
     * </ul>
     */
    @Test(groups = {"fe", "admin-panel"})
    public void migrationTasksTest() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        DatabaseUtil.insertMigrationTask(EVENT, NAME, false, false);
        AccessTokenActions.invalidateAccessToken(driver, getServerPort());
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MIGRATION_TASKS);

        MigrationTask task = AwaitilityWrapper.getWithWait(() -> MigrationTasksActions.findMigrationTaskByEventValidated(driver, EVENT))
            .orElseThrow(() -> new IllegalStateException("MigrationTask not found."));
        assertThat(task.isCompleted()).isFalse();
        task.trigger(driver);

        task = AwaitilityWrapper.getWithWait(() -> MigrationTasksActions.findMigrationTaskByEventValidated(driver, EVENT), MigrationTask::isCompleted)
            .orElseThrow(() -> new RuntimeException("MigrationTask is not completed."));

        task.delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> MigrationTasksActions.findMigrationTaskByEvent(driver, EVENT).isEmpty())
            .assertTrue("MigrationTask is not deleted.");
    }
}
