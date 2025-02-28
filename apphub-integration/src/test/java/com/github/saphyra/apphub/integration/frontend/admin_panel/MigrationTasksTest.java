package com.github.saphyra.apphub.integration.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.MigrationTasksActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
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

    @Test(groups = {"fe", "admin-panel"})
    public void migrationTasksTest() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        DatabaseUtil.insertMigrationTask(EVENT, NAME, false, false);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MIGRATION_TASKS);

        MigrationTask task = MigrationTasksActions.findMigrationTaskByEventValidated(driver, EVENT);
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
