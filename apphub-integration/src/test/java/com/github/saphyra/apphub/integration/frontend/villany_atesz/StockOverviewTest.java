package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
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

import static org.assertj.core.api.Assertions.assertThat;

public class StockOverviewTest extends SeleniumTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final String MEASUREMENT = "measurement";
    private static final Integer IN_CAR = 100;
    private static final Integer IN_STORAGE = 200;
    private static final Integer AMOUNT = 20;

    @Test(groups = {"fe", "villany-atesz"})
    public void overview() {
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
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME, "", IN_CAR, IN_STORAGE, 0);
        VillanyAteszNavigation.openStockOverview(driver);

        StockItemOverview item = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        item.toCar();
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_OVERVIEW_ZERO_AMOUNT);
        ToastMessageUtil.clearToasts(driver);

        item.toStorage();
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_OVERVIEW_ZERO_AMOUNT);
        ToastMessageUtil.clearToasts(driver);

        item.setAmount(AMOUNT);
        item.toCar();

        AwaitilityWrapper.awaitAssert(
            () -> item,
            i -> assertThat(i)
                .returns(IN_CAR + AMOUNT + " " + MEASUREMENT, StockItemOverview::getInCar)
                .returns(IN_STORAGE - AMOUNT + " " + MEASUREMENT, StockItemOverview::getInStorage)
        );

        item.setAmount(AMOUNT);
        item.toStorage();

        AwaitilityWrapper.awaitAssert(
            () -> item,
            i -> assertThat(i)
                .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
                .returns(IN_STORAGE + " " + MEASUREMENT, StockItemOverview::getInStorage)
        );
    }
}
