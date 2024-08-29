package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszContactsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockOverviewPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.Cart;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.CartItem;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartCrudTest extends SeleniumTest {
    private static final String CONTACT_NAME = "contact-name";
    private static final String CATEGORY_NAME = "category-name";
    private static final String MEASUREMENT = "measurement";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final int IN_CAR = 100;
    private static final int PRICE = 50;
    private static final String CONTACT_CODE = "contact-code";
    private static final Integer AMOUNT = 20;
    private static final int MARGIN = 2;

    @Test(groups = {"fe", "villany-atesz"})
    public void cartCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createContact(driver, CONTACT_NAME, CONTACT_CODE);
        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, MEASUREMENT);
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME, "", "", IN_CAR, 0, PRICE);

        //Create cart
        VillanyAteszNavigation.openContacts(driver);
        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .createCart(driver);

        //Add to cart
        StockItemOverview item = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        //Zero amount
        item.setAmount(0);
        item.addToCart();
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_STOCK_ZERO_AMOUNT);

        //Add
        item.setAmount(AMOUNT);
        item.addToCart();

        AwaitilityWrapper.awaitAssert(
            () -> VillanyAteszStockOverviewPageActions.getCartDetails(driver),
            cart -> assertThat(cart.orElseThrow())
                .returns((int) ((double) AMOUNT * PRICE * Constants.CART_DEFAULT_MARGIN), Cart::getTotalValue)
                .extracting(Cart::getItems)
                .extracting(cartItems -> cartItems.get(0))
                .returns(AMOUNT, CartItem::getAmount)
                .returns(STOCK_ITEM_NAME, CartItem::getName)
        );

        CustomAssertions.singleListAssertThat(VillanyAteszStockOverviewPageActions.getItems(driver))
            .returns(IN_CAR - AMOUNT + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(true, StockItemOverview::isInCart)
            .returns(AMOUNT + " " + MEASUREMENT, StockItemOverview::getInCart);

        //Margin
        Cart cart = VillanyAteszStockOverviewPageActions.getCartDetails(driver)
            .orElseThrow();

        cart.setMargin(MARGIN);

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszStockOverviewPageActions.getCartDetails(driver).orElseThrow(), c -> assertThat(c.getTotalValue()).isEqualTo(AMOUNT * PRICE * MARGIN));

        //Remove

        cart.getItems()
            .stream()
            .findFirst()
            .orElseThrow()
            .remove(driver);

        AwaitilityWrapper.awaitAssert(
            () -> VillanyAteszStockOverviewPageActions.getItems(driver),
            items -> CustomAssertions.singleListAssertThat(items)
                .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
                .returns(false, StockItemOverview::isInCart)
                .returns("", StockItemOverview::getInCart)
        );

        //Delete cart
        item.setAmount(AMOUNT);
        item.addToCart();

        VillanyAteszStockOverviewPageActions.getCartDetails(driver)
            .orElseThrow()
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockOverviewPageActions.getCartDetails(driver).isEmpty())
            .assertTrue();

        assertThat(VillanyAteszStockOverviewPageActions.getActiveCartLabel(driver)).isEqualTo(Constants.SELECT_OPTION_CHOOSE);

        CustomAssertions.singleListAssertThat(VillanyAteszStockOverviewPageActions.getItems(driver))
            .returns(IN_CAR + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(false, StockItemOverview::isInCart)
            .returns("", StockItemOverview::getInCart);

        //Finalize cart
        VillanyAteszNavigation.openContacts(driver);
        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .createCart(driver);

        item = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();

        item.setAmount(AMOUNT);
        item.addToCart();

        VillanyAteszStockOverviewPageActions.getCartDetails(driver)
            .orElseThrow()
            .finalize(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszStockOverviewPageActions.getCartDetails(driver).isEmpty())
            .assertTrue();

        assertThat(VillanyAteszStockOverviewPageActions.getActiveCartLabel(driver)).isEqualTo(Constants.SELECT_OPTION_CHOOSE);

        CustomAssertions.singleListAssertThat(VillanyAteszStockOverviewPageActions.getItems(driver))
            .returns(IN_CAR - AMOUNT + " " + MEASUREMENT, StockItemOverview::getInCar)
            .returns(false, StockItemOverview::isInCart)
            .returns("", StockItemOverview::getInCart);
    }
}
