package com.github.saphyra.apphub.integration.action.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutionResult;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.github.saphyra.apphub.integration.core.TestBase.EXECUTOR_SERVICE;

@Slf4j
public class SkyXploreUtils {
    public static void registerAndNavigateToMainMenu(int serverPort, List<BiWrapper<WebDriver, RegistrationParameters>> users) {
        List<FutureWrapper<Void>> futures = users.stream()
            .map(biWrapper -> registerAndNavigateToMainMenu(serverPort, biWrapper))
            .toList();

        futures.stream()
            .map(FutureWrapper::get)
            .forEach(ExecutionResult::getOrThrow);
    }

    private static FutureWrapper<Void> registerAndNavigateToMainMenu(int serverPort, BiWrapper<WebDriver, RegistrationParameters> biWrapper) {
        return registerAndNavigateToMainMenu(serverPort, biWrapper.getEntity1(), biWrapper.getEntity2());
    }

    private static FutureWrapper<Void> registerAndNavigateToMainMenu(int serverPort, WebDriver driver, RegistrationParameters userData) {
        return EXECUTOR_SERVICE.execute(() -> {
            Navigation.toIndexPage(serverPort, driver);
            IndexPageActions.registerUser(driver, userData);

            ModulesPageActions.openModule(serverPort, driver, ModuleLocation.SKYXPLORE);
            SleepUtil.sleep(1000);
            SkyXploreCharacterActions.submitForm(driver);

            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
                .assertTrue("MainMenu is not loaded.");
        });
    }
}
