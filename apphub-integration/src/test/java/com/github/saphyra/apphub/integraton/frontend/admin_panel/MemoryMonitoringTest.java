package com.github.saphyra.apphub.integraton.frontend.admin_panel;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.memory_monitoring.MemoryMonitoringActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryMonitoringTest extends SeleniumTest {
    private static final List<String> SERVICES = ImmutableList.of(
        "event-gateway",
        "admin-panel",
        "localization",
        "main-gateway",
        "message-sender",
        "modules",
        "notebook",
        "scheduler",
        "skyxplore-data",
        "skyxplore-game",
        "skyxplore-lobby",
        "training",
        "user",
        "utils",
        "web-content",
        "community",
        "diary"
    );

    @Test
    public void memoryMonitoring() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.MEMORY_MONITORING);


        List<String> services = AwaitilityWrapper.getListWithWait(() -> MemoryMonitoringActions.getReportContainers(driver), webElements -> webElements.size() == SERVICES.size())
            .stream()
            .map(webElement -> webElement.getAttribute("id").split("report-container-")[1])
            .collect(Collectors.toList());

        assertThat(services).containsExactlyInAnyOrderElementsOf(SERVICES);
    }
}
