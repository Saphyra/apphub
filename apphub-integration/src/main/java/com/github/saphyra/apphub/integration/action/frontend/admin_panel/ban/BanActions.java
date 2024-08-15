package com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.CurrentBan;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFillContentEditable;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.selectOptionByValue;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.setCheckboxState;
import static org.assertj.core.api.Assertions.assertThat;


public class BanActions {
    public static void searchUser(WebDriver driver, String searchText) {
        clearAndFill(driver.findElement(By.id("ban-search-input")), searchText);
        driver.findElement(By.id("ban-search-button"))
            .click();
    }

    public static List<WebElement> getSearchResult(WebDriver driver) {
        return driver.findElements(By.className("ban-user"));
    }

    public static boolean isUserDetailsPageOpened(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("ban-details"));
    }

    public static void setUpAdminForm(WebDriver driver, String bannedRole, boolean permanent, int duration, String chronoUnit, String reason, String password) {
        selectOptionByValue(driver.findElement(By.id("ban-user-role-to-ban-select")), bannedRole);
        setCheckboxState(driver.findElement(By.id("ban-user-is-permanent-input")), permanent);
        if (!permanent) {
            clearAndFill(driver.findElement(By.id("ban-user-duration")), String.valueOf(duration));
            selectOptionByValue(driver.findElement(By.id("ban-user-banned-until-input")), chronoUnit);
        }
        clearAndFillContentEditable(driver, driver.findElement(By.id("ban-user-ban-password")), password);
        clearAndFill(driver.findElement(By.id("ban-user-reason")), reason);
    }

    public static void submitBanForm(WebDriver driver) {
        driver.findElement(By.id("ban-user-ban"))
            .click();
    }

    public static List<CurrentBan> getCurrentBans(WebDriver driver) {
        return driver.findElements(By.className("ban-user-banned-role"))
            .stream()
            .map(CurrentBan::new)
            .collect(Collectors.toList());
    }

    public static void confirmAccountDeletion(WebDriver driver) {
        driver.findElement(By.id("ban-user-schedule-deletion-confirm-button"))
            .click();
    }

    public static void fillDeleteUserTime(WebDriver driver, LocalDate date, Integer hours, Integer minutes) {
        WebElementUtils.clearAndFillDateTime(driver.findElement(By.id("ban-user-schedule-deletion-at")), date, hours, minutes);
    }

    public static void fillDeleteUserPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("ban-user-schedule-deletion-password")), password);
    }

    public static boolean isUserMarkedForDeletion(WebDriver driver) {
        String text = driver.findElement(By.id("ban-user-deletion-is-marked-for-deletion"))
            .getText();

        assertThat(text).isIn("Yes", "No");

        return text.equals("Yes");
    }

    public static String getUserMarkedForDeletionAt(WebDriver driver) {
        return driver.findElement(By.id("ban-user-scheduled-for-deletion-at"))
            .getText();
    }

    public static void unmarkForDeletion(WebDriver driver) {
        driver.findElement(By.id("ban-user-cancel-deletion"))
            .click();
    }

    public static void fillRevokeBanPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("ban-user-banned-role-password")), password);
    }

    public static void confirmRevoke(WebDriver driver) {
        driver.findElement(By.id("ban-user-banned-role-confirm-revoke-button"))
            .click();
    }

    public static void confirmScheduleDeletion(WebDriver driver) {
        driver.findElement(By.id("ban-user-schedule-deletion-confirm-button"))
            .click();
    }

    public static void confirmUnmarkForDeletion(WebDriver driver) {
        driver.findElement(By.id("ban-user-cancel-deletion-confirm-button"))
            .click();
    }

    public static void scheduleForDeletion(WebDriver driver) {
        driver.findElement(By.id("ban-user-schedule-deletion"))
            .click();
    }
}
