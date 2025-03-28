package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class VillanyAteszCommissionActions {
    public static void setDaysOfWork(WebDriver driver, Integer daysOfWork) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-days-of-work")), daysOfWork);
    }

    public static void setHoursPerDay(WebDriver driver, Integer hoursPerDay) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-hours-per-day")), hoursPerDay);
    }

    public static void setHourlyWage(WebDriver driver, Integer hourlyWage) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-hourly-wage")), hourlyWage);
    }

    public static void setDepartureFee(WebDriver driver, Integer departureFee) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-departure-fee")), departureFee);
    }

    public static void setExtraCost(WebDriver driver, Integer extraCost) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-extra-cost")), extraCost);
    }

    public static void decreaseMargin(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-commission-margin-minus-10-percent-button"))
            .click();
    }

    public static void setDeposit(WebDriver driver, Integer deposit) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-commission-deposit")), deposit);
    }

    public static void selectCart(WebDriver driver, String contactName) {
        WebElementUtils.selectOptionByLabel(driver.findElement(By.id("villany-atesz-commission-cart-selector")), contactName);
    }

    public static void createNew(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-commissions-new-button"))
            .click();
    }

    public static List<String> getCommissions(WebDriver driver) {
        return WebElementUtils.getSelectOptions(driver.findElement(By.id("villany-atesz-commission-selector")));
    }

    public static void selectCommission(WebDriver driver, String label) {
        WebElementUtils.selectOptionByLabel(driver.findElement(By.id("villany-atesz-commission-selector")), label);
    }

    public static Integer getDaysOfWork(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-days-of-work")).getDomAttribute("value"));
    }

    public static Integer getHoursPerDay(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-hours-per-day")).getDomAttribute("value"));
    }

    public static Integer getHourlyWage(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-hourly-wage")).getDomAttribute("value"));
    }

    public static Integer getDepartureFee(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-departure-fee")).getDomAttribute("value"));
    }

    public static Integer getTotalWage(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-total-wage")).getText().replace(" ", ""));
    }

    public static Integer getExtraCost(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-extra-cost")).getDomAttribute("value"));
    }

    public static String getCartContactName(WebDriver driver) {
        return WebElementUtils.getSelectedOptionLabel(driver.findElement(By.id("villany-atesz-commission-cart-selector")));
    }

    public static Double getCartCost(WebDriver driver) {
        return Double.parseDouble(driver.findElement(By.id("villany-atesz-commission-cart-cost")).getText());
    }

    public static Double getMaterialCost(WebDriver driver) {
        return Double.parseDouble(driver.findElement(By.id("villany-atesz-commission-material-cost")).getText().replace(" ", ""));
    }

    public static Integer getMargin(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-margin-percent")).getText());
    }

    public static Double getTotalMaterialCost(WebDriver driver) {
        return Double.parseDouble(driver.findElement(By.id("villany-atesz-commission-total-material-cost")).getText().replace(" ", ""));
    }

    public static Double getToBePaid(WebDriver driver) {
        return Double.parseDouble(driver.findElement(By.id("villany-atesz-commission-to-be-paid")).getText().replace(" ", ""));
    }

    public static Integer getDeposit(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-commission-deposit")).getDomAttribute("value").replace(" ", ""));
    }

    public static Double getRemaining(WebDriver driver) {
        return Double.parseDouble(driver.findElement(By.id("villany-atesz-commission-remaining")).getText().replace(" ", ""));
    }

    public static void deleteCommission(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-delete-commission-button"))
            .click();

        driver.findElement(By.id("villany-atesz-commission-deletion-confirm-button"))
            .click();
    }

    public static boolean isCommissionSelectorVisible(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("villany-atesz-commission-selector"));
    }
}
