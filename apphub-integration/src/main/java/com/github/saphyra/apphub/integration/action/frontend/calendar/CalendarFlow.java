package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.github.saphyra.apphub.integration.core.TestBase.EXECUTOR_SERVICE;

public class CalendarFlow {
    public static List<Context> init(int serverPort, List<WebDriver> drivers) {
        List<FutureWrapper<Context>> futures = drivers.stream()
            .map(driver -> EXECUTOR_SERVICE.asyncProcess(() -> init(serverPort, driver)))
            .toList();

        return futures.stream()
            .map(contextFutureWrapper -> contextFutureWrapper.get().getOrThrow())
            .toList();
    }

    public static Context init(int serverPort, WebDriver driver) {
        Navigation.toIndexPage(serverPort, driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(serverPort, driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);
        return new Context(driver, userData);
    }

    public static record Context(WebDriver driver, RegistrationParameters userData) {
    }
}
