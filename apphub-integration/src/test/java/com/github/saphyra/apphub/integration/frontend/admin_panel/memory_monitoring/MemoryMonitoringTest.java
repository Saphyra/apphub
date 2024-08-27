package com.github.saphyra.apphub.integration.frontend.admin_panel.memory_monitoring;

import com.github.saphyra.apphub.integration.action.frontend.admin_panel.memory_monitoring.MemoryMonitoringActions;
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
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MemoryMonitoringTest extends SeleniumTest {
    @Test(groups = {"fe", "admin-panel"})
    public void memoryMonitoring() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MEMORY_MONITORING);

        assertServicesLoaded(driver);

        hideAll(driver);
        showAll(driver);
        hideSpecific(driver);
        showSpecific(driver);
    }

    private static void hideAll(WebDriver driver) {
        MemoryMonitoringActions.hideAll(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> MemoryMonitoringActions.getReportContainers(driver).isEmpty())
            .assertTrue("Services are not hidden.");
    }

    private static void showAll(WebDriver driver) {
        MemoryMonitoringActions.showAll(driver);

        assertServicesLoaded(driver);
    }

    private void hideSpecific(WebDriver driver) {
        MemoryMonitoringActions.toggleService(driver, Constants.SERVICE_NAME_USER);

        AwaitilityWrapper.createDefault()
            .until(() -> !MemoryMonitoringActions.getDisplayedServices(driver).contains(Constants.SERVICE_NAME_USER))
            .assertTrue("Specific service is not hidden.");
    }

    private void showSpecific(WebDriver driver) {
        MemoryMonitoringActions.toggleService(driver, Constants.SERVICE_NAME_USER);

        AwaitilityWrapper.createDefault()
            .until(() -> MemoryMonitoringActions.getDisplayedServices(driver).contains(Constants.SERVICE_NAME_USER))
            .assertTrue("Specific service is not shown.");
    }

    private static void assertServicesLoaded(WebDriver driver) {
        List<String> services = AwaitilityWrapper.getListWithWait(() -> MemoryMonitoringActions.getDisplayedServices(driver), strings -> strings.size() == Constants.SERVICES.size());

        assertThat(services).containsExactlyInAnyOrderElementsOf(Constants.SERVICES);
    }
}
