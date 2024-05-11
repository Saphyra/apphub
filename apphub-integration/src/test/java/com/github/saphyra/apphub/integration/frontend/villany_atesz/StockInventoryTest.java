package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszContactsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockInventoryPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockOverviewPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemInventory;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StockInventoryTest extends SeleniumTest {
    private static final String CATEGORY_NAME_1 = "category-name-1";
    private static final String CATEGORY_NAME_2 = "category-name-2";
    private static final String MEASUREMENT = "measurement";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final String SERIAL_NUMBER = "serial-number";
    private static final Integer IN_CAR = 423;
    private static final Integer IN_STORAGE = 324;
    private static final String NEW_STOCK_ITEM_NAME = "new-stock-item-name";
    private static final String CONTACT_NAME = "contact-name";
    private static final String CONTACT_CODE = "contact-code";
    private static final Integer AMOUNT = 20;

    @Test(groups = {"fe", "villany-atesz"})
    public void inventory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME_1, MEASUREMENT);
        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME_2, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME_1, STOCK_ITEM_NAME, "", 0, 0, 0);

        VillanyAteszNavigation.openStockInventory(driver);

        StockItemInventory item = VillanyAteszStockInventoryPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        item.setInventoried(true);
        item.setCategory(CATEGORY_NAME_2);
        item.setName(" ");
        item.setSerialNumber(SERIAL_NUMBER);
        item.setInCar(IN_CAR);
        item.setInStorage(IN_STORAGE);

        SleepUtil.sleep(2000);
        assertThat(item.isNamePending()).isTrue();

        item.setName(NEW_STOCK_ITEM_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> !item.isNamePending())
            .assertTrue("Item name is still pending");

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockOverviewPageActions.getItems(driver), items -> !items.isEmpty(), items -> items.get(0))
            .returns(CATEGORY_NAME_2, StockItemOverview::getCategoryName)
            .returns(NEW_STOCK_ITEM_NAME, StockItemOverview::getName)
            .returns(SERIAL_NUMBER, StockItemOverview::getSerialNumber)
            .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(IN_STORAGE + " " + MEASUREMENT, StockItemOverview::getInStorage);

        VillanyAteszNavigation.openStockInventory(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszStockInventoryPageActions.getItems(driver), items -> !items.isEmpty(), items -> items.get(0))
            .returns(true, StockItemInventory::isInventoried);
    }

    @Test(groups = {"fe", "villany-atesz"})
    public void inCartAndDeletion() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME_1, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME_1, STOCK_ITEM_NAME, "", 100, 0, 0);
        VillanyAteszUtils.createContact(driver, CONTACT_NAME, CONTACT_CODE);

        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .createCart(driver);

        StockItemOverview itemOverview = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();
        itemOverview.setAmount(AMOUNT);
        itemOverview.addToCart();

        //Unmodifiable if in cart
        VillanyAteszNavigation.openStockInventory(driver);

        StockItemInventory itemInventory = VillanyAteszStockInventoryPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        assertThat(itemInventory.isInCart()).isTrue();
        itemInventory.delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockInventoryPageActions.getItems(driver).isEmpty())
            .assertTrue();

        //Item disappeared from cart
        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszStockOverviewPageActions.getCartDetails(driver).orElseThrow(), cart -> assertThat(cart.getItems()).isEmpty());
    }
}
