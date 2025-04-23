package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszCommissionActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
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
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommissionsTest extends SeleniumTest {
    private static final Integer DAYS_OF_WORK = 5;
    private static final Integer HOURS_PER_DAY = 8;
    private static final Integer HOURLY_WAGE = 7500;
    private static final Integer DEPARTURE_FEE = 15000;
    private static final Integer EXTRA_COST = 20000;
    private static final Integer DEPOSIT = 6000;
    private static final String CONTACT_NAME = "contact-name";
    private static final String CATEGORY_NAME = "category-name";
    private static final String STOCK_ITEM_NAME = "stock-item-name";
    private static final int STOCK_ITEM_PRICE = 200;
    private static final Integer STOCK_ITEM_AMOUNT = 75;
    private static final int COMMISSION_MARGIN = 50;
    private static final String UNKNOWN_COMMISSION = "Unknown commission";

    @Test(groups = {"villany-atesz", "fe"})
    public void commissionCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.VILLANY_ATESZ);

        //Setup
        VillanyAteszUtils.createContact(driver, CONTACT_NAME, "");
        VillanyAteszUtils.createCategory(driver, CATEGORY_NAME, "");
        VillanyAteszUtils.createStockItem(driver, CATEGORY_NAME, STOCK_ITEM_NAME, "", "", 100, 0, STOCK_ITEM_PRICE);
        VillanyAteszUtils.createCart(driver, CONTACT_NAME);
        StockItemOverview item = VillanyAteszStockOverviewPageActions.getItems(driver)
            .stream()
            .findFirst()
            .orElseThrow();
        item.setAmount(STOCK_ITEM_AMOUNT);
        item.addToCart();

        VillanyAteszNavigation.openCommodities(driver);

        //Set up commission
        VillanyAteszCommissionActions.setDaysOfWork(driver, DAYS_OF_WORK);

        VillanyAteszCommissionActions.selectCart(driver, CONTACT_NAME);

        VillanyAteszCommissionActions.setHoursPerDay(driver, HOURS_PER_DAY);
        VillanyAteszCommissionActions.setHourlyWage(driver, HOURLY_WAGE);
        VillanyAteszCommissionActions.setDepartureFee(driver, DEPARTURE_FEE);

        VillanyAteszCommissionActions.setExtraCost(driver, EXTRA_COST);
        for (int i = 0; i < 5; i++) {
            VillanyAteszCommissionActions.decreaseMargin(driver);
        }
        VillanyAteszCommissionActions.setDeposit(driver, DEPOSIT);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszCommissionActions.modificationsSaved(driver))
            .assertTrue("Not all modifications were saved");

        //Create new
        VillanyAteszCommissionActions.createNew(driver);

        List<String> commissions = VillanyAteszCommissionActions.getCommissions(driver);
        assertThat(commissions).hasSize(2);
        assertThat(commissions.get(0)).isEqualTo(UNKNOWN_COMMISSION);

        //Verify save and calculations
        VillanyAteszCommissionActions.selectCommission(driver, commissions.get(1));

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(VillanyAteszCommissionActions.getDaysOfWork(driver)).isEqualTo(DAYS_OF_WORK);
            assertThat(VillanyAteszCommissionActions.getHoursPerDay(driver)).isEqualTo(HOURS_PER_DAY);
            assertThat(VillanyAteszCommissionActions.getHourlyWage(driver)).isEqualTo(HOURLY_WAGE);
            assertThat(VillanyAteszCommissionActions.getDepartureFee(driver)).isEqualTo(DEPARTURE_FEE);
            int totalWage = DAYS_OF_WORK * HOURS_PER_DAY * HOURLY_WAGE + DEPARTURE_FEE;
            assertThat(VillanyAteszCommissionActions.getTotalWage(driver)).isEqualTo(totalWage);

            assertThat(VillanyAteszCommissionActions.getExtraCost(driver)).isEqualTo(EXTRA_COST);
            assertThat(VillanyAteszCommissionActions.getCartContactName(driver)).isEqualTo(CONTACT_NAME);
            double cartCost = STOCK_ITEM_AMOUNT * STOCK_ITEM_PRICE * 1.2;
            assertThat(VillanyAteszCommissionActions.getCartCost(driver)).isEqualTo(cartCost);
            assertThat(VillanyAteszCommissionActions.getMaterialCost(driver)).isEqualTo(cartCost + EXTRA_COST);
            assertThat(VillanyAteszCommissionActions.getMargin(driver)).isEqualTo(COMMISSION_MARGIN);
            double totalMaterialCost = COMMISSION_MARGIN / 100d * (cartCost + EXTRA_COST);
            assertThat(VillanyAteszCommissionActions.getTotalMaterialCost(driver)).isEqualTo(totalMaterialCost);

            assertThat(VillanyAteszCommissionActions.getToBePaid(driver)).isEqualTo(totalMaterialCost + totalWage);
            assertThat(VillanyAteszCommissionActions.getDeposit(driver)).isEqualTo(DEPOSIT);
            assertThat(VillanyAteszCommissionActions.getRemaining(driver)).isEqualTo(totalMaterialCost + totalWage - DEPOSIT);
        });

        //Delete commission
        VillanyAteszCommissionActions.deleteCommission(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(VillanyAteszCommissionActions.isCommissionSelectorVisible(driver)).isFalse());
    }
}
