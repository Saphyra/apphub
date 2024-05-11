package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockNewItemPageActions;
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
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class StockNewItemTest extends SeleniumTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String MEASUREMENT = "measurement";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 342;
    private static final Integer IN_STORAGE = 536;
    private static final Integer PRICE = 5;

    @Test(groups = {"fe", "villany-atesz"})
    public void creteStockItem() {
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
        VillanyAteszNavigation.openStockNewItem(driver);

        noCategorySelected(driver);
        blankName(driver);
        create(driver);
    }

    private void noCategorySelected(WebDriver driver) {
        VillanyAteszStockNewItemPageActions.fillForm(driver, Constants.SELECT_OPTION_CHOOSE, STOCK_ITEM_NAME, SERIAL_NUMBER, IN_CAR, IN_STORAGE, PRICE);

        VillanyAteszStockNewItemPageActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_NEW_ITEM_CHOOSE_CATEGORY);
    }

    private void blankName(WebDriver driver) {
        VillanyAteszStockNewItemPageActions.fillForm(driver, CATEGORY_NAME, " ", SERIAL_NUMBER, IN_CAR, IN_STORAGE, PRICE);

        VillanyAteszStockNewItemPageActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_NEW_ITEM_BLANK_NAME);
    }

    private void create(WebDriver driver) {
        VillanyAteszStockNewItemPageActions.fillForm(driver, CATEGORY_NAME, STOCK_ITEM_NAME, SERIAL_NUMBER, IN_CAR, IN_STORAGE, PRICE);

        VillanyAteszStockNewItemPageActions.submit(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_NEW_ITEM_CREATED);

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockOverviewPageActions.getItems(driver), list -> !list.isEmpty(), list -> list.get(0))
            .returns(CATEGORY_NAME, StockItemOverview::getCategoryName)
            .returns(STOCK_ITEM_NAME, StockItemOverview::getName)
            .returns(SERIAL_NUMBER, StockItemOverview::getSerialNumber)
            .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(IN_STORAGE + " " + MEASUREMENT, StockItemOverview::getInStorage)
            .returns(PRICE + Constants.FT_SUFFIX, StockItemOverview::getPrice)
            .returns(PRICE * (IN_CAR + IN_STORAGE) + Constants.FT_SUFFIX, StockItemOverview::getStockPrice);
    }
}
