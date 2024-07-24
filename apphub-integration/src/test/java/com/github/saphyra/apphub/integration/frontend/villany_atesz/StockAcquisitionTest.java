package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszAcquisitionActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockAcquisitionPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockInventoryPageActions;
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
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.AcquisitionHistoryItem;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemAcquisition;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemInventory;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StockAcquisitionTest extends SeleniumTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final String MEASUREMENT = "measurement";
    private static final Integer IN_CAR = 42;
    private static final Integer IN_STORAGE = 46;
    private static final int DEFAULT_PRICE = 100;
    private static final Integer PRICE = DEFAULT_PRICE + 1;
    private static final String BAR_CODE = "bar-code";
    private static final String NEW_BAR_CODE = "new-bar-code";
    private static final Integer FORCED_PRICE = DEFAULT_PRICE - 1;

    @Test(groups = {"fe", "villany-atesz"})
    public void stockAcquisition() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME, "", "", 0, 0, DEFAULT_PRICE);
        VillanyAteszNavigation.openAcquisition(driver);

        addItem(driver);
        removeItem(driver);
        addItem(driver);

        noCategoryChosen(driver);
        noStockItemChosen(driver);
        acquire(driver);
        acquire_forceUpdatePrice(driver);

        checkAcquisitionHistory(driver);
    }

    @Test(groups = {"fe", "villany-atesz"})
    public void searchByBarCode() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME, "", BAR_CODE, 0, 0, 0);
        VillanyAteszNavigation.openAcquisition(driver);

        addItem(driver);

        StockItemAcquisition item = VillanyAteszStockAcquisitionPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        item.setBarCodeSearch(BAR_CODE);

        AwaitilityWrapper.awaitAssert(() -> item, stockItemAcquisition -> {
            assertThat(stockItemAcquisition.getCategory()).isEqualTo(CATEGORY_NAME);
            assertThat(stockItemAcquisition.getItemName()).isEqualTo(STOCK_ITEM_NAME);
            assertThat(stockItemAcquisition.getBarCode()).isEqualTo(BAR_CODE);
        });

        item.setBarCode(NEW_BAR_CODE);

        VillanyAteszStockAcquisitionPageActions.addToStock(driver);

        driver.findElement(By.id("villany-atesz-stock-acquisition-confirm-button"))
            .click();

        VillanyAteszNavigation.openStockInventory(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockInventoryPageActions.getItems(driver))
            .returns(NEW_BAR_CODE, StockItemInventory::getBarCode);
    }

    private void acquire(WebDriver driver) {
        StockItemAcquisition item = VillanyAteszStockAcquisitionPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();
        item.chooseItem(STOCK_ITEM_NAME);
        item.setToCar(IN_CAR);
        item.setToStorage(IN_STORAGE);
        item.setPrice(PRICE);

        VillanyAteszStockAcquisitionPageActions.addToStock(driver);

        driver.findElement(By.id("villany-atesz-stock-acquisition-confirm-button"))
            .click();

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_ACQUISITION_ITEMS_STORED);

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockOverviewPageActions.getItems(driver), stockItemOverviews -> !stockItemOverviews.isEmpty(), stockItemOverviews -> stockItemOverviews.get(0))
            .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(IN_STORAGE + " " + MEASUREMENT, StockItemOverview::getInStorage)
            .returns(PRICE + Constants.FT_SUFFIX, StockItemOverview::getPrice);
    }

    private void acquire_forceUpdatePrice(WebDriver driver) {
        VillanyAteszNavigation.openAcquisition(driver);
        addItem(driver);

        VillanyAteszStockAcquisitionPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .chooseCategory(CATEGORY_NAME)
            .chooseItem(STOCK_ITEM_NAME)
            .setPrice(FORCED_PRICE)
            .setForceUpdatePrice(true);

        VillanyAteszStockAcquisitionPageActions.addToStock(driver);

        driver.findElement(By.id("villany-atesz-stock-acquisition-confirm-button"))
            .click();

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockOverviewPageActions.getItems(driver), stockItemOverviews -> !stockItemOverviews.isEmpty(), stockItemOverviews -> stockItemOverviews.get(0))
            .returns(FORCED_PRICE + Constants.FT_SUFFIX, StockItemOverview::getPrice);
    }

    private void checkAcquisitionHistory(WebDriver driver) {
        VillanyAteszNavigation.openAcquisition(driver);

        VillanyAteszAcquisitionActions.openHistory(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszAcquisitionActions.getHistoryItems(driver))
            .returns(IN_CAR + IN_STORAGE, AcquisitionHistoryItem::getAmount)
            .returns(STOCK_ITEM_NAME, AcquisitionHistoryItem::getItemName);
    }

    private void noStockItemChosen(WebDriver driver) {
        VillanyAteszStockAcquisitionPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .chooseCategory(CATEGORY_NAME);

        VillanyAteszStockAcquisitionPageActions.addToStock(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_ACQUISITION_CHOOSE_STOCK_ITEM);
    }

    private void noCategoryChosen(WebDriver driver) {
        VillanyAteszStockAcquisitionPageActions.addToStock(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_ACQUISITION_CHOOSE_CATEGORY);
    }

    private void removeItem(WebDriver driver) {
        VillanyAteszStockAcquisitionPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .remove();

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockAcquisitionPageActions.getItems(driver).isEmpty())
            .assertTrue("Item is not removed.");
    }

    private void addItem(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-acquisition-new-item-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !VillanyAteszStockAcquisitionPageActions.getItems(driver).isEmpty())
            .assertTrue("No item added.");
    }
}
