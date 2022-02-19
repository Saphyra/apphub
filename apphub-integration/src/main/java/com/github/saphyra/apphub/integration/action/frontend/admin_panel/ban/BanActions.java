package com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.structure.admin_panel.CurrentBan;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.selectOption;
import static com.github.saphyra.apphub.integration.framework.WebElementUtils.setCheckboxState;


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
}
