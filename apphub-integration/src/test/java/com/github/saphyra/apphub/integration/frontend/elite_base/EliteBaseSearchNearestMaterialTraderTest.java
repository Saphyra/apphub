package com.github.saphyra.apphub.integration.frontend.elite_base;

import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBasePageActions;
import com.github.saphyra.apphub.integration.action.frontend.elite_base.EliteBaseSearchNearestMaterialTraderPageActions;
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

public class EliteBaseSearchNearestMaterialTraderTest extends SeleniumTest {
    @Test(groups = {"fe", "elite-base"})
    public void searchNearestMaterialTrader() {
        String starName = EliteBasePreconditions.checkMaterialTraderPreconditions();

        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.ELITE_BASE);

        EliteBasePageActions.openSubpage(driver, EliteBaseSubPage.MATERIAL_TRADER);

        EliteBaseSearchNearestMaterialTraderPageActions.setReferenceStarSystem(driver, starName);
        EliteBaseSearchNearestMaterialTraderPageActions.search(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(EliteBaseSearchNearestMaterialTraderPageActions.getResult(driver)).isNotEmpty());
    }
}
