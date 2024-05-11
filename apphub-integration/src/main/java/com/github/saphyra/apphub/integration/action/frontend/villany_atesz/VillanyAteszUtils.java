package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import org.openqa.selenium.WebDriver;

public class VillanyAteszUtils {
    public static void createCategory(WebDriver driver, String name, String measurement) {
        VillanyAteszNavigation.openStockCategories(driver);

        VillanyAteszStockCategoriesPageActions.fillCreateForm(driver, name, measurement);

        VillanyAteszStockCategoriesPageActions.submitCreateForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !VillanyAteszStockCategoriesPageActions.getStockCategories(driver).isEmpty())
            .assertTrue("StockCategory not created");
    }

    public static void createStockItem(WebDriver driver, String categoryName, String stockItemName, String serialNumber, int inCar, int inStorage, int price) {
        VillanyAteszNavigation.openStockNewItem(driver);

        VillanyAteszStockNewItemPageActions.fillForm(driver, categoryName, stockItemName, serialNumber, inCar, inStorage, price);
        VillanyAteszStockNewItemPageActions.submit(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_NEW_ITEM_CREATED);
    }

    public static void createContact(WebDriver driver, String contactName, String contactCode) {
        VillanyAteszNavigation.openContacts(driver);

        VillanyAteszContactsPageActions.setNameInputValue(driver, contactName);
        VillanyAteszContactsPageActions.setCodeInputValue(driver, contactCode);

        VillanyAteszContactsPageActions.submitSaveContact(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !VillanyAteszContactsPageActions.getContacts(driver).isEmpty())
            .assertTrue();
    }
}
