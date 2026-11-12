package com.github.saphyra.apphub.integration.frontend.elite_base;

import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBasePageActions;
import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBaseTradePageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.util.EliteBasePreconditions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.elite_base.EliteBaseSubPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EliteBaseTradeTest extends SeleniumTest {
    private static final String BIOWASTE = "biowaste";

    /**
     * Tests buying commodities in the Elite Base trade page.
     *
     * <ul>
     *   <li>Verifies that a buy search returns results when commodity and reference system are set.</li>
     * </ul>
     */
    @Test(groups = {"fe", "elite-base"})
    public void buyCommodities() {
        String starName = EliteBasePreconditions.checkBuyCommodityPreconditions(BIOWASTE);

        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.ELITE_BASE);

        EliteBasePageActions.openSubpage(driver, EliteBaseSubPage.BUY_COMMODITIES);

        EliteBaseTradePageActions.setCommodity(driver, BIOWASTE);
        EliteBaseTradePageActions.setReferenceSystem(driver, starName);
        EliteBaseTradePageActions.search(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(EliteBaseTradePageActions.getSearchResult(driver)).isNotEmpty());
    }

    /**
     * Tests selling commodities in the Elite Base trade page.
     *
     * <ul>
     *   <li>Verifies that a sell search returns results when commodity and reference system are set.</li>
     * </ul>
     */
    @Test(groups = {"fe", "elite-base"})
    public void sellCommodities() {
        String starName = EliteBasePreconditions.checkSellCommodityPreconditions(BIOWASTE);

        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.ELITE_BASE);

        EliteBasePageActions.openSubpage(driver, EliteBaseSubPage.SELL_COMMODITIES);

        EliteBaseTradePageActions.setCommodity(driver, BIOWASTE);
        EliteBaseTradePageActions.setReferenceSystem(driver, starName);
        EliteBaseTradePageActions.search(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(EliteBaseTradePageActions.getSearchResult(driver)).isNotEmpty());
    }
}
