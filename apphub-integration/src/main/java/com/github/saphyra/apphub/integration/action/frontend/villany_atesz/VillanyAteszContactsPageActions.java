package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.Contact;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszContactsPageActions {
    public static void submitSaveContact(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-contacts-save-button"))
            .click();
    }

    public static void fillContactData(WebDriver driver, String code, String name, String phone, String address, String note) {
        setCodeInputValue(driver, code);
        setNameInputValue(driver, name);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-contacts-phone-input")), phone);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-contacts-address-input")), address);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-contacts-note-input")), note);
    }

    public static void setCodeInputValue(WebDriver driver, String code) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-contacts-code-input")), code);
    }

    public static void setNameInputValue(WebDriver driver, String name) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-contacts-name-input")), name);
    }

    public static List<Contact> getContacts(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-contacts-contact"))
            .stream()
            .map(Contact::new)
            .collect(Collectors.toList());
    }

    public static String getCodeInputValue(WebDriver driver) {
        return driver.findElement(By.id("villany-atesz-contacts-code-input"))
            .getAttribute("value");
    }

    public static String getNameInputValue(WebDriver driver) {
        return driver.findElement(By.id("villany-atesz-contacts-name-input"))
            .getAttribute("value");
    }

    public static String getPhoneInputValue(WebDriver driver) {
        return driver.findElement(By.id("villany-atesz-contacts-phone-input"))
            .getAttribute("value");
    }

    public static String getAddressInputValue(WebDriver driver) {
        return driver.findElement(By.id("villany-atesz-contacts-address-input"))
            .getAttribute("value");
    }

    public static String getNoteInputValue(WebDriver driver) {
        return driver.findElement(By.id("villany-atesz-contacts-note-input"))
            .getAttribute("value");
    }
}
