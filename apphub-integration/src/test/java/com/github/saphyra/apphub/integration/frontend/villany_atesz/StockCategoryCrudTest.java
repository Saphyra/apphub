package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszContactsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockCategoriesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockOverviewPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockCategory;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StockCategoryCrudTest extends SeleniumTest {
    private static final String NAME = "name";
    private static final String MEASUREMENT = "measurement";
    private static final String NEW_NAME = "new-name";
    private static final String NEW_MEASUREMENT = "new-measurement";
    private static final String CONTACT_NAME = "contact-name";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final int IN_CAR = 432;
    private static final Integer AMOUNT = 23;
    private static final String CONTACT_CODE = "contact-code";

    @Test(groups = {"fe", "villany-atesz"})
    public void stockCategoryCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);
        VillanyAteszNavigation.openStockCategories(driver);

        create_blankName(driver);
        create(driver);

        edit_blankName(driver);
        edit(driver);

        delete(driver);
    }

    private void delete(WebDriver driver) {
        VillanyAteszStockCategoriesPageActions.getStockCategories(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockCategoriesPageActions.getStockCategories(driver).isEmpty())
            .assertTrue("StockCategory is not deleted.");
    }

    private void edit(WebDriver driver) {
        StockCategory stockCategory = VillanyAteszStockCategoriesPageActions.getStockCategories(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        stockCategory.setName(NEW_NAME);
        stockCategory.setMeasurement(NEW_MEASUREMENT);

        stockCategory.save();

        AwaitilityWrapper.createDefault()
            .until(() -> !stockCategory.editingEnabled())
            .assertTrue("Modifications are not saved.");

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockCategoriesPageActions.getStockCategories(driver), stockCategories -> !stockCategories.isEmpty(), stockCategories -> stockCategories.get(0))
            .returns(NEW_NAME, StockCategory::getName)
            .returns(NEW_MEASUREMENT, StockCategory::getMeasurement);
    }

    private void edit_blankName(WebDriver driver) {
        StockCategory stockCategory = VillanyAteszStockCategoriesPageActions.getStockCategories(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        stockCategory.edit();

        stockCategory.setName(" ");
        stockCategory.save();

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_CATEGORIES_BLANK_NAME);
    }

    private void create_blankName(WebDriver driver) {
        VillanyAteszStockCategoriesPageActions.submitCreateForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_CATEGORIES_BLANK_NAME);
    }

    private void create(WebDriver driver) {
        VillanyAteszStockCategoriesPageActions.fillCreateForm(driver, NAME, MEASUREMENT);

        VillanyAteszStockCategoriesPageActions.submitCreateForm(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockCategoriesPageActions.getStockCategories(driver), stockCategories -> !stockCategories.isEmpty(), stockCategories -> stockCategories.get(0))
            .returns(NAME, StockCategory::getName)
            .returns(MEASUREMENT, StockCategory::getMeasurement);
    }

    @Test(groups = {"fe", "villany-atesz"})
    public void deleteStockCategoryWithRelatedStockAndCartItems() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createContact(driver, CONTACT_NAME, CONTACT_CODE);
        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .createCart(driver);
        VillanyAteszUtils.createCategory(driver, NAME, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, NAME, STOCK_ITEM_NAME, "", "", IN_CAR, 0, 0);
        VillanyAteszNavigation.openStockOverview(driver);
        StockItemOverview item = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();
        item.setAmount(AMOUNT);
        item.addToCart();

        VillanyAteszNavigation.openStockCategories(driver);

        VillanyAteszStockCategoriesPageActions.getStockCategories(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockCategoriesPageActions.getStockCategories(driver).isEmpty())
            .assertTrue();

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockOverviewPageActions.getItems(driver).isEmpty());

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszStockOverviewPageActions.getCartDetails(driver).orElseThrow(), cart -> assertThat(cart.getItems()).isEmpty());
    }
}
