package com.github.saphyra.apphub.integration.action.frontend;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutionResult;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.BiConsumer;

import static com.github.saphyra.apphub.integration.core.TestBase.EXECUTOR_SERVICE;

public class RegistrationUtils {
    public static void registerUsers(int serverPort, List<BiWrapper<WebDriver, RegistrationParameters>> users) {
        registerUsers(serverPort, users, (driver, registrationParameters) -> {
        });
    }

    public static void registerUsers(int serverPort, List<BiWrapper<WebDriver, RegistrationParameters>> users, BiConsumer<WebDriver, RegistrationParameters> followUp) {
        List<FutureWrapper<Void>> futures = users.stream()
            .map(biWrapper -> EXECUTOR_SERVICE.execute(() -> {
                Navigation.toIndexPage(serverPort, biWrapper.getEntity1());
                IndexPageActions.registerUser(biWrapper.getEntity1(), biWrapper.getEntity2());
                followUp.accept(biWrapper.getEntity1(), biWrapper.getEntity2());
            }))
            .toList();

        futures.stream()
            .map(FutureWrapper::get)
            .forEach(ExecutionResult::getOrThrow);
    }
}
