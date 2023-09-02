package com.github.saphyra.apphub.integration.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.community.Blacklist;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "community"})
    void blacklistCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        RegistrationUtils.registerUsers(
            List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)),
            (driver, registrationParameters) -> ModulesPageActions.openModule(driver, ModuleLocation.COMMUNITY)
        );

        CommunityActions.openBlacklistTab(driver1);

        search_userNotFound(driver1);
        search_queryTooShort(driver1);
        Blacklist blacklist = blacklistUser(driver1, userData2);
        deleteBlacklist(driver1, blacklist);
    }

    private static void search_userNotFound(WebDriver driver1) {
        BlacklistActions.fillSearchForm(driver1, UUID.randomUUID().toString());

        BlacklistActions.verifyUserNotFound(driver1);
    }

    private static void search_queryTooShort(WebDriver driver1) {
        BlacklistActions.fillSearchForm(driver1, "as");

        BlacklistActions.verifyQueryTooShort(driver1);
    }

    private static Blacklist blacklistUser(WebDriver driver1, RegistrationParameters userData2) {
        BlacklistActions.createBlacklist(driver1, userData2.getUsername());

        List<Blacklist> blacklists = BlacklistActions.getBlacklist(driver1);

        assertThat(blacklists).hasSize(1);

        Blacklist blacklist = blacklists.get(0);
        assertThat(blacklist.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(blacklist.getEmail()).isEqualTo(userData2.getEmail());
        return blacklist;
    }

    private static void deleteBlacklist(WebDriver driver1, Blacklist blacklist) {
        blacklist.delete();

        CommonPageActions.confirmConfirmationDialog(driver1, "delete-blacklist-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver1, "User unblocked");

        AwaitilityWrapper.createDefault()
            .until(() -> BlacklistActions.getBlacklist(driver1).isEmpty())
            .assertTrue("Blacklist is not empty.");
    }
}
