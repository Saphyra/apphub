package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.action.frontend.villany_atesz.VillanyAteszStockOverviewPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class Contact {
    private final WebElement webElement;

    public String getCode() {
        return webElement.findElement(By.className("villany-atesz-contacts-contact-code"))
            .getText();
    }

    public String getName() {
        return webElement.findElement(By.className("villany-atesz-contacts-contact-name"))
            .getText();
    }

    public String getPhone() {
        return webElement.findElement(By.className("villany-atesz-contacts-contact-phone"))
            .getText();
    }

    public String getAddress() {
        return webElement.findElement(By.className("villany-atesz-contacts-contact-address"))
            .getText();
    }

    public String getNote() {
        return webElement.findElement(By.className("villany-atesz-contacts-contact-note"))
            .getText();
    }

    public void edit() {
        webElement.click();
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-contacts-contact-delete-button"))
            .click();

        driver.findElement(By.id("villany-atesz-delete-contact-confirm-button"))
            .click();
    }

    public void createCart(WebDriver driver) {
        String code = getCode();
        String name = getName();

        webElement.findElement(By.className("villany-atesz-contacts-contact-create-cart-button"))
            .click();

        AwaitilityWrapper.awaitAssert(() -> VillanyAteszStockOverviewPageActions.getActiveCartLabel(driver), cartLabel -> assertThat(cartLabel).isEqualTo(String.format("%s - %s", code, name).trim()));
    }
}
