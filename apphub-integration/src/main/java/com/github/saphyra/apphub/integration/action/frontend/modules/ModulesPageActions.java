package com.github.saphyra.apphub.integration.action.frontend.modules;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.Category;
import com.github.saphyra.apphub.integration.structure.api.modules.Favorite;
import com.github.saphyra.apphub.integration.structure.api.modules.Module;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ModulesPageActions {
    public static void logout(int serverPort, WebDriver driver) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_PAGE));

        ModulesPage.logoutButton(driver).click();
        AwaitilityWrapper.awaitAssert(driver::getCurrentUrl, currentUrl -> assertThat(currentUrl).isEqualTo(UrlFactory.create(serverPort, GenericEndpoints.INDEX_PAGE)));

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.INDEX_SUCCESSFULLY_LOGGED_OUT);
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
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(ModulesPage.SEARCH_INPUT));
        clearAndFill(ModulesPage.searchInput(driver), search);
    }

    public static List<Category> getCategories(WebDriver driver) {
        return ModulesPage.getCategories(driver)
            .stream()
            .map(Category::new)
            .collect(Collectors.toList());
    }

    public static void openModule(int serverPort, WebDriver driver, ModuleLocation moduleLocation) {
        log.debug("Opening module {}", moduleLocation);
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_PAGE)))
            .assertTrue("Modules page is not loaded.");

        getModule(driver, moduleLocation)
            .orElseThrow(() -> new RuntimeException("Module not found for moduleLocation " + moduleLocation))
            .open();

        AwaitilityWrapper.createDefault()
            .until(() -> moduleLocation.pageLoaded(driver))
            .assertTrue(String.format("Failed to open module %s. Current url: %s", moduleLocation, driver.getCurrentUrl()));
    }

    public static Optional<Module> getModule(WebDriver driver, ModuleLocation moduleLocation) {
        return AwaitilityWrapper.getListWithWait(() -> getCategories(driver), categories -> !categories.isEmpty())
            .stream()
            .filter(category -> category.getCategoryId().equals(moduleLocation.getCategoryId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Category not found for moduleLocation " + moduleLocation))
            .getModules()
            .stream()
            .filter(module -> module.getModuleId().equals(moduleLocation.getModuleId()))
            .findFirst();
    }

    public static Category findCategoryByIdValidated(WebDriver driver, String categoryId) {
        return getCategories(driver)
            .stream()
            .filter(category -> category.getCategoryId().equals(categoryId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Category not found by id " + categoryId));
    }
}
