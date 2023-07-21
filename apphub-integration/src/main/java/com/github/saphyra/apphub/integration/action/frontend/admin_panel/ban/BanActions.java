package com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.CurrentBan;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.selectOption;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.setCheckboxState;
import static org.assertj.core.api.Assertions.assertThat;


public class BanActions {
    public static void searchUser(WebDriver driver, String searchText) {
        clearAndFill(BanPage.search(driver), searchText);
        BanPage.searchButton(driver).click();
    }

    public static List<WebElement> getSearchResult(WebDriver driver) {
        return BanPage.searchResultContent(driver);
    }

    public static boolean isUserDetailsPageOpened(WebDriver driver) {
        return BanPage.userDetailsPage(driver).isDisplayed();
    }

    public static void setUpAdminForm(WebDriver driver, String bannedRole, boolean permanent, int duration, String chronoUnit, String reason, String password) {
        selectOption(BanPage.bannableRoles(driver), bannedRole);
        setCheckboxState(BanPage.permanentCheckbox(driver), permanent);
        clearAndFill(BanPage.durationInput(driver), String.valueOf(duration));
        selectOption(BanPage.chronoUnit(driver), chronoUnit);
        clearAndFill(BanPage.reasonTextarea(driver), reason);
        clearAndFill(BanPage.passwordInput(driver), password);
    }

    public static void submitBanForm(WebDriver driver) {
        BanPage.createBanButton(driver).click();
    }

    public static List<CurrentBan> getCurrentBans(WebDriver driver) {
        return BanPage.currentBans(driver)
            .stream()
            .map(CurrentBan::new)
            .collect(Collectors.toList());
    }

    public static void submitDeleteAccountForm(WebDriver driver) {
        BanPage.deleteTheUserButton(driver)
            .click();
    }

    public static void fillDeleteUserDate(WebDriver driver, LocalDate date) {
        WebElementUtils.clearAndFillDate(BanPage.deleteTheUserAtDate(driver), date);
    }

    public static void fillDeleteUserTime(WebDriver driver, Integer hours, Integer minutes) {
        WebElementUtils.clearAndFillTime(BanPage.deleteTheUserAtTime(driver), hours, minutes);
    }

    public static void fillDeleteUserPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(BanPage.confirmDeleteUserPassword(driver), password);
    }

    public static boolean isUserMarkedForDeletion(WebDriver driver) {
        String text = BanPage.userMarkedForDeletion(driver)
            .getText();

        assertThat(text).isIn("Igen", "Nem");

        return text.equals("Igen");
    }

    public static String getUserMarkedForDeletionAt(WebDriver driver) {
        return BanPage.userMarkedForDeletionAt(driver)
            .getText();
    }

    public static void unmarkForDeletion(WebDriver driver) {
        BanPage.unmarkForDeletionButton(driver).click();
    }
}
