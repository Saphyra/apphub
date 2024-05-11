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
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.Contact;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactsCrudTest extends SeleniumTest {
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";
    private static final String NEW_CODE = "new-code";
    private static final String NEW_NAME = "new-name";
    private static final String NEW_PHONE = "new-phone";
    private static final String NEW_ADDRESS = "new-address";
    private static final String NEW_NOTE = "new-note";

    @Test(groups = {"fe", "villany-atesz"})
    public void contactsCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);
        VillanyAteszNavigation.openContacts(driver);

        createContact_blankName(driver);
        createContact(driver);

        editContact_inputsFilledProperly(driver);
        editContact_blankName(driver);
        editContact(driver);

        deleteContact(driver);
    }

    private void deleteContact(WebDriver driver) {
        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No contact found."))
            .delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getContacts(driver).isEmpty())
            .assertTrue("Contact not deleted.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getCodeInputValue(driver).equals(""))
            .assertTrue("Code is not cleared.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getNameInputValue(driver).equals(""))
            .assertTrue("Name is not cleared.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getPhoneInputValue(driver).equals(""))
            .assertTrue("Phone is not cleared.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getAddressInputValue(driver).equals(""))
            .assertTrue("Address is not cleared.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getNoteInputValue(driver).equals(""))
            .assertTrue("Note is not cleared.");
    }

    private void editContact(WebDriver driver) {
        VillanyAteszContactsPageActions.fillContactData(driver, NEW_CODE, NEW_NAME, NEW_PHONE, NEW_ADDRESS, NEW_NOTE);
    }

    private void editContact_inputsFilledProperly(WebDriver driver) {
        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No contact found."))
            .edit();

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getCodeInputValue(driver).equals(CODE))
            .assertTrue("Code is not set.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getNameInputValue(driver).equals(NAME))
            .assertTrue("Name is not set.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getPhoneInputValue(driver).equals(PHONE))
            .assertTrue("Phone is not set.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getAddressInputValue(driver).equals(ADDRESS))
            .assertTrue("Address is not set.");

        AwaitilityWrapper.createDefault()
            .until(() -> VillanyAteszContactsPageActions.getNoteInputValue(driver).equals(NOTE))
            .assertTrue("Note is not set.");
    }

    private void editContact_blankName(WebDriver driver) {
        VillanyAteszContactsPageActions.setNameInputValue(driver, " ");

        VillanyAteszContactsPageActions.submitSaveContact(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_CONTACTS_BLANK_NAME);
    }

    private void createContact(WebDriver driver) {
        VillanyAteszContactsPageActions.fillContactData(driver, CODE, NAME, PHONE, ADDRESS, NOTE);
        VillanyAteszContactsPageActions.submitSaveContact(driver);

        AwaitilityWrapper.assertWithWaitList(() -> VillanyAteszContactsPageActions.getContacts(driver), list -> !list.isEmpty(), list -> list.get(0))
            .returns(CODE, Contact::getCode)
            .returns(NAME, Contact::getName)
            .returns(PHONE, Contact::getPhone)
            .returns(ADDRESS, Contact::getAddress)
            .returns(NOTE, Contact::getNote);
    }

    private void createContact_blankName(WebDriver driver) {
        VillanyAteszContactsPageActions.submitSaveContact(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.VILLANY_ATESZ_CONTACTS_BLANK_NAME);
    }

    @Test(groups = {"fe", "villany-atesz"})
    public void deleteContactAndCorrespondingCart() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);
        SleepUtil.sleep(2000);
        driver.navigate()
            .refresh();
        ModulesPageActions.openModule(driver, ModuleLocation.VILLANY_ATESZ);

        VillanyAteszUtils.createContact(driver, NAME, CODE);

        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .createCart(driver);

        VillanyAteszNavigation.openContacts(driver);

        VillanyAteszContactsPageActions.getContacts(driver)
            .stream()
            .findFirst()
            .orElseThrow()
            .delete(driver);

        VillanyAteszNavigation.openStockOverview(driver);

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszStockOverviewPageActions.getActiveCartLabel(driver), label -> assertThat(label).isEqualTo(Constants.SELECT_OPTION_CHOOSE));

        ToastMessageUtil.verifyNoNotifications(driver);
    }
}
