package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockInventoryPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszIndexPageTest extends SeleniumTest {
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME_1 = "stock-item-name-1";
    private static final String STOCK_ITEM_NAME_2 = "stock-item-name-2";
    private static final int IN_CAR_1 = 243;
    private static final int IN_CAR_2 = 2434;
    private static final int IN_STORAGE_1 = 42;
    private static final int IN_STORAGE_2 = 442;
    private static final int PRICE_1 = 32;
    private static final int PRICE_2 = 324;

    @Test(groups = {"fe", "villany-atesz"})
    public void totalValue() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, "");
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME_1, "", "", IN_CAR_1, IN_STORAGE_1, PRICE_1);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME_2, "", "", IN_CAR_2, IN_STORAGE_2, PRICE_2);

        VillanyAteszNavigation.openIndex(driver);

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszIndexPageActions.getTotalValue(driver), integer -> assertThat(integer).isEqualTo((IN_CAR_1 + IN_STORAGE_1) * PRICE_1 + (IN_CAR_2 + IN_STORAGE_2) * PRICE_2));
    }

    @Test(groups = {"fe", "villany-atesz"})
    public void itemsMarkedForAcquisition() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, "");
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME_1, "", "", IN_CAR_1, IN_STORAGE_1, PRICE_1);

        VillanyAteszNavigation.openStockInventory(driver);

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszStockInventoryPageActions.getItems(driver))
            .markForAcquisition(true);

        VillanyAteszNavigation.openIndex(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszIndexPageActions.getItemsMarkedForAcquisition(driver)).isEqualTo(STOCK_ITEM_NAME_1);
    }
}
