package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinGroupManagerActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PinGroupTest extends SeleniumTest {
    private static final String LIST_ITEM_TITLE_1 = "list-item-title-1";
    private static final String PIN_GROUP_NAME = "pin-group-name";
    private static final String NEW_PIN_GROUP_NAME = "new-pin-group-name";
    private static final String LIST_ITEM_TITLE_2 = "list-item-title-2";

    @Test(groups = {"fe", "notebook"})
    void openParentOfPinnedItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newOnlyTitle(driver, LIST_ITEM_TITLE_1);
        NotebookUtils.newOnlyTitle(driver, LIST_ITEM_TITLE_2);
        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_TITLE_1)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_TITLE_2)
            .pin(driver);

        //Create - blank name
        PinActions.openPinGroupManager(driver);

        PinGroupManagerActions.createPinGroupWithName(driver, " ");

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_PIN_GROUP_NAME_BLANK);

        //Create - too long name
        PinGroupManagerActions.createPinGroupWithName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_PIN_GROUP_NAME_TOO_LONG);

        //Create
        PinGroupManagerActions.createPinGroupWithName(driver, PIN_GROUP_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> PinGroupManagerActions.findPinGroupByName(driver, PIN_GROUP_NAME).isPresent())
            .assertTrue("PinGroup did not appear in PinGroupManager");

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.findPinGroupByName(driver, PIN_GROUP_NAME).isPresent())
            .assertTrue("PinGroup did not appear in PinGroup list");

        //Rename - blank name
        PinGroupManagerActions.findPinGroupByNameValidated(driver, PIN_GROUP_NAME)
            .openRenameDialog(driver);

        PinGroupManagerActions.renamePinGroup(driver, " ");

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_PIN_GROUP_NAME_BLANK);

        //Rename too long name
        PinGroupManagerActions.renamePinGroup(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_PIN_GROUP_NAME_TOO_LONG);

        //Rename
        PinGroupManagerActions.renamePinGroup(driver, NEW_PIN_GROUP_NAME);

        AwaitilityWrapper.createDefault()
            .until(() -> PinGroupManagerActions.findPinGroupByName(driver, NEW_PIN_GROUP_NAME).isPresent())
            .assertTrue("PinGroup did not appear in PinGroupManager");

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.findPinGroupByName(driver, NEW_PIN_GROUP_NAME).isPresent())
            .assertTrue("PinGroup did not appear in PinGroup list");

        //Add item to group
        PinGroupManagerActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME)
            .expand();

        WebElementUtils.dragAndDrop(
            driver,
            PinActions.findPinnedItemByTitle(driver, LIST_ITEM_TITLE_1).getWebElement(),
            PinActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME).getWebElement()
        );

        PinActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME)
            .open(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.getPinnedItems(driver).size() == 1)
            .assertTrue("Content of active PinGroup is not updated.");

        AwaitilityWrapper.createDefault()
            .until(() -> !PinGroupManagerActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME).getItems().isEmpty())
            .assertTrue("Newly added list item did not appear in PinGroupManagerPinGroup items.");

        //Remove from group
        PinGroupManagerActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME)
            .getItems()
            .get(0)
            .remove();

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.getPinnedItems(driver).isEmpty())
            .assertTrue("Item is not removed from pinned items.");

        AwaitilityWrapper.createDefault()
            .until(() -> PinGroupManagerActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME).getItems().isEmpty())
            .assertTrue("Item is not removed from PinGroupManagerPinGroup items.");

        //Delete group
        PinGroupManagerActions.findPinGroupByNameValidated(driver, NEW_PIN_GROUP_NAME)
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> PinGroupManagerActions.getPinGroups(driver).isEmpty())
            .assertTrue("PinGroup is not removed from PinGroupManager");

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.getPinGroups(driver).stream().noneMatch(pinGroup -> pinGroup.getName().equals(NEW_PIN_GROUP_NAME)))
            .assertTrue("PinGroup is not removed from PinGroups");

        assertThat(PinActions.findPinGroupByNameValidated(driver, Constants.DEFAULT_PIN_GROUP_NAME).isOpened()).isTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.getPinnedItems(driver).size() == 2)
            .assertTrue("PinGroup items are not updated.");
    }
}
