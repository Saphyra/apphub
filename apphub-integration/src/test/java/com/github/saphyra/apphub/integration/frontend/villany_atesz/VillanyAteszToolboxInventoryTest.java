package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszToolboxInventoryPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszToolboxPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolInventoryItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszToolboxInventoryTest extends SeleniumTest {
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 42;
    private static final String TOOL_TYPE_NAME = "tool-type-name";
    private static final String STORAGE_BOX_NAME = "storage-box-name";
    private static final String NEW_TOOL_TYPE = "new-tool-type";
    private static final String NEW_BRAND = "new-brand";
    private static final String NEW_NAME = "new-name";
    private static final String NEW_STORAGE_BOX = "new-storage-box";
    private static final Integer NEW_COST = 4;

    @Test(groups = {"fe", "villany-atesz"})
    public void toolboxInventory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszNavigation.openToolbox(driver);
        VillanyAteszNavigation.openToolboxNew(driver);

        VillanyAteszToolboxPageActions.createToolbox(driver, BRAND, NAME, COST, TOOL_TYPE_NAME, STORAGE_BOX_NAME);

        VillanyAteszNavigation.openToolboxInventory(driver);

        ToolInventoryItem item = AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxInventoryPageActions.getItems(driver));
        item.editToolType(NEW_TOOL_TYPE);
        item.editBrand(NEW_BRAND);
        item.editName(NEW_NAME);
        item.editStorageBox(NEW_STORAGE_BOX);
        item.editCost(NEW_COST);
        item.editStatus(ToolStatus.DAMAGED);
        item.setInventoried(true);

        SleepUtil.sleep(3000);
        VillanyAteszNavigation.openToolboxOverview(driver);
        VillanyAteszNavigation.openToolboxInventory(driver);

        item = AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxInventoryPageActions.getItems(driver));

        assertThat(item)
            .returns(true, ToolInventoryItem::isInventoried)
            .returns(NEW_TOOL_TYPE, ToolInventoryItem::getToolType)
            .returns(NEW_BRAND, ToolInventoryItem::getBrand)
            .returns(NEW_NAME, ToolInventoryItem::getName)
            .returns(NEW_STORAGE_BOX, ToolInventoryItem::getStorageBox)
            .returns(NEW_COST, ToolInventoryItem::getCost)
            .returns(ToolStatus.DAMAGED, ToolInventoryItem::getToolStatus);

        VillanyAteszToolboxInventoryPageActions.resetInventoried(driver);

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszToolboxInventoryPageActions.getItems(driver), toolInventoryItems -> CustomAssertions.singleListAssertThat(toolInventoryItems).returns(false, ToolInventoryItem::isInventoried));

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxInventoryPageActions.getItems(driver))
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxInventoryPageActions.getItems(driver).isEmpty())
            .assertTrue("Tool not deleted.");
    }
}
