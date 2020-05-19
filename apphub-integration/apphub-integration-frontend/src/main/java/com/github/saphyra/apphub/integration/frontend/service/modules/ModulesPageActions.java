package com.github.saphyra.apphub.integration.frontend.service.modules;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.Category;
import com.github.saphyra.apphub.integration.frontend.model.modules.Favorite;
import com.github.saphyra.apphub.integration.frontend.model.modules.Module;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

public class ModulesPageActions {
    public static void logout(WebDriver driver) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.MODULES_PAGE));

        ModulesPage.logoutButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.WEB_ROOT)));

        NotificationUtil.verifySuccessNotification(driver, "Sikeres kijelentkezés.");
    }

    public static List<Module> getModules(WebDriver driver) {
        return ModulesPage.getModules(driver)
            .stream()
            .map(Module::new)
            .collect(Collectors.toList());
    }

    public static List<Favorite> getFavorites(WebDriver driver) {
        return ModulesPage.getFavorites(driver)
            .stream()
            .map(Favorite::new)
            .collect(Collectors.toList());
    }

    public static void search(WebDriver driver, String search) {
        clearAndFill(ModulesPage.searchInput(driver), search);
    }

    public static List<Category> getCategories(WebDriver driver) {
        return ModulesPage.getCategories(driver)
            .stream()
            .map(Category::new)
            .collect(Collectors.toList());
    }
}
