package com.github.saphyra.apphub.integraton.frontend.community;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.community.Blacklist;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistCrudTest extends SeleniumTest {
    @Test(groups = "community")
    void blacklistCrud() {
        WebDriver driver1 = extractDriver();
        WebDriver driver2 = extractDriver();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        List<Future<Void>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(biWrapper -> headToMainMenu(biWrapper.getEntity1(), biWrapper.getEntity2()))
            .collect(Collectors.toList());

        for (int i = 0; i < 120; i++) {
            if (futures.stream().allMatch(Future::isDone)) {
                break;
            }

            SleepUtil.sleep(1000);
        }

        CommunityActions.openBlacklistTab(driver1);

        //Search - User not found
        BlacklistActions.fillSearchForm(driver1, UUID.randomUUID().toString());

        BlacklistActions.verifyUserNotFound(driver1);

        //Search - Query too short
        BlacklistActions.fillSearchForm(driver1, "as");

        BlacklistActions.verifyQueryTooShort(driver1);

        //Blacklist user
        BlacklistActions.createBlacklist(driver1, userData2.getUsername());

        List<Blacklist> blacklists = BlacklistActions.getBlacklist(driver1);

        assertThat(blacklists).hasSize(1);

        Blacklist blacklist = blacklists.get(0);
        assertThat(blacklist.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(blacklist.getEmail()).isEqualTo(userData2.getEmail());

        //Delete blacklist
        blacklist.delete();

        CommonPageActions.confirmConfirmationDialog(driver1, "delete-blacklist-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver1, "Tiltás feloldva.");

        AwaitilityWrapper.createDefault()
            .until(() -> BlacklistActions.getBlacklist(driver1).isEmpty())
            .assertTrue("Blacklist is not empty.");
    }

    private Future<Void> headToMainMenu(WebDriver driver, RegistrationParameters userData) {
        return EXECUTOR_SERVICE.submit(() -> {
            Navigation.toIndexPage(driver);
            IndexPageActions.registerUser(driver, userData);
            ModulesPageActions.openModule(driver, ModuleLocation.COMMUNITY);
            return null;
        });
    }
}
