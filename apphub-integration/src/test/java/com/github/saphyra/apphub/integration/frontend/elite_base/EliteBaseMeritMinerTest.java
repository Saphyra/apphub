package com.github.saphyra.apphub.integration.frontend.elite_base;

import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBaseMeritMinerPageActions;
import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBasePageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.elite_base.EliteBaseSubPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EliteBaseMeritMinerTest extends SeleniumTest {
    /**
     * Tests that the Merit Miner page displays offers after user login and navigation.
     *
     * <ul>
     *   <li>Verifies that offers list is not empty when accessing Merit Miner subpage.</li>
     * </ul>
     */
    @Test(groups = {"fe", "elite-base"})
    public void meritMiner() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.ELITE_BASE);

        EliteBasePageActions.openSubpage(driver, EliteBaseSubPage.MERIT_MINER);

        EliteBaseMeritMinerPageActions.load(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(EliteBaseMeritMinerPageActions.getOffers(driver)).isNotEmpty());
    }
}
