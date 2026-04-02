package com.github.saphyra.apphub.integration.frontend.monitoring;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.monitoring.MonitoringPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringTest extends SeleniumTest {
    @Test(groups = {"fe", "monitoring"})
    public void monitoringTest() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        driver.navigate().refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.MONITORING);

        MonitoringPageActions.selectFeature(driver, Feature.MEMORY_MONITORING);
        MonitoringPageActions.selectFunctionality(driver, Constants.MONITORING_FUNCTIONALITY_MEMORY_STATUS);
        AwaitilityWrapper.awaitAssert(() -> assertThat(MonitoringPageActions.getServices(driver)).containsAll(Constants.SERVICES));

        MonitoringPageActions.load(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(MonitoringPageActions.getMetricServices(driver)).containsAll(Constants.SERVICES));
    }
}
