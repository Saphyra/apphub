package com.github.saphyra.apphub.integration.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszNavigation;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszToolboxManagePageActions;
import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszToolboxPageActions;
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
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ScrappedTool;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolOverviewItem;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolboxManagedStorageBox;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolboxManagedToolType;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolboxCrudTest extends SeleniumTest {
    private static final String BRAND = "brand";
    private static final Integer COST = 43;
    private static final String NAME = "name";
    private static final String TOOL_TYPE_NAME = "tool-type-name";
    private static final String STORAGE_BOX_NAME = "storage-box-name";
    private static final String NEW_TOOL_TYPE_NAME = "new-tool-type-name";
    private static final String NEW_STORAGE_BOX_NAME = "new-storage-box-name";

    @Test(groups = {"fe", "villany-atesz"})
    public void toolboxCrud() {
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

        create_blankName(driver);
        create(driver);

        markAsLost(driver);
        markAsDamaged(driver);
        scrapTool(driver);

        descrapTool(driver);

        deleteTool(driver);

        VillanyAteszNavigation.openToolboxManage(driver);
        editToolType(driver);
        deleteToolType(driver);

        editStorageBox(driver);
        deleteStorageBox(driver);
    }

    private void deleteStorageBox(WebDriver driver) {
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxManagePageActions.getStorageBoxes(driver))
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxManagePageActions.getStorageBoxes(driver).isEmpty())
            .assertTrue("StorageBox is not deleted.");
    }

    private void editStorageBox(WebDriver driver) {
        ToolboxManagedStorageBox storageBox = AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxManagePageActions.getStorageBoxes(driver));
        storageBox.edit(NEW_STORAGE_BOX_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> !storageBox.editingEnabled())
            .assertTrue("EditingEnables id still true.");


        assertThat(storageBox.getName()).isEqualTo(NEW_STORAGE_BOX_NAME);
    }

    private void deleteToolType(WebDriver driver) {
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxManagePageActions.getToolTypes(driver))
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxManagePageActions.getToolTypes(driver).isEmpty())
            .assertTrue("ToolType is not deleted.");
    }

    private void editToolType(WebDriver driver) {
        ToolboxManagedToolType toolType = AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxManagePageActions.getToolTypes(driver));
        toolType.edit(NEW_TOOL_TYPE_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> !toolType.editingEnabled())
            .assertTrue("EditingEnables id still true.");

        assertThat(toolType.getName()).isEqualTo(NEW_TOOL_TYPE_NAME);
    }

    private void deleteTool(WebDriver driver) {
        VillanyAteszNavigation.openToolboxOverview(driver);

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxPageActions.getOverviewTools(driver))
            .scrap();

        VillanyAteszNavigation.openToolboxScrapped(driver);

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> VillanyAteszToolboxPageActions.getScrappedTools(driver))
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxPageActions.getScrappedTools(driver).isEmpty())
            .assertTrue("Scrapped tool is not deleted.");
    }

    private void descrapTool(WebDriver driver) {
        VillanyAteszToolboxPageActions.getScrappedTools(driver)
            .get(0)
            .descrap();

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxPageActions.getScrappedTools(driver).isEmpty())
            .assertTrue("Descrapped tool is still present");
    }

    private void scrapTool(WebDriver driver) {
        VillanyAteszToolboxPageActions.getOverviewTools(driver)
            .get(0)
            .scrap();

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszToolboxPageActions.getOverviewTools(driver).isEmpty())
            .assertTrue("Scrapped tool is still present.");

        VillanyAteszNavigation.openToolboxScrapped(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszToolboxPageActions.getScrappedTools(driver))
            .returns(LocalDate.now(), ScrappedTool::getScrappedAt);
    }

    private void markAsDamaged(WebDriver driver) {
        VillanyAteszToolboxPageActions.getOverviewTools(driver)
            .get(0)
            .markAsDamaged();

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszToolboxPageActions.getOverviewTools(driver))
            .returns(ToolStatus.DAMAGED, ToolOverviewItem::getStatus);
    }

    private void markAsLost(WebDriver driver) {
        VillanyAteszToolboxPageActions.getOverviewTools(driver)
            .get(0)
            .markAsLost();

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszToolboxPageActions.getOverviewTools(driver))
            .returns(ToolStatus.LOST, ToolOverviewItem::getStatus);
    }

    private void create(WebDriver driver) {
        VillanyAteszToolboxPageActions.fillForm(driver, BRAND, NAME, COST, TOOL_TYPE_NAME, STORAGE_BOX_NAME);
        VillanyAteszToolboxPageActions.submit(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.VILLANY_ATESZ_TOOLBOX_NEW_CREATED);

        VillanyAteszNavigation.openToolboxOverview(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszToolboxPageActions.getOverviewTools(driver))
            .returns(BRAND, ToolOverviewItem::getBrand)
            .returns(NAME, ToolOverviewItem::getName)
            .returns(COST, ToolOverviewItem::getCost)
            .returns(LocalDate.now(), ToolOverviewItem::getAcquiredAt)
            .returns(ToolStatus.DEFAULT, ToolOverviewItem::getStatus)
            .returns(TOOL_TYPE_NAME, ToolOverviewItem::getToolType)
            .returns(STORAGE_BOX_NAME, ToolOverviewItem::getStorageBox);
    }

    private void create_blankName(WebDriver driver) {
        VillanyAteszNavigation.openToolboxNew(driver);

        VillanyAteszToolboxPageActions.fillForm(driver, BRAND, " ", COST, TOOL_TYPE_NAME, STORAGE_BOX_NAME);
        VillanyAteszToolboxPageActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_TOOLBOX_NEW_NAME_MUST_NOT_BE_BLANK);
    }
}