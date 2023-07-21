package com.github.saphyra.apphub.integraton.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Friend;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class FriendshipCrudTest extends SeleniumTest {
    @Test(groups = "skyxplore")
    public void friendshipCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        List<Future<Void>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(biWrapper -> headToMainMenu(biWrapper.getEntity1(), biWrapper.getEntity2()))
            .toList();

        for (int i = 0; i < 120; i++) {
            if (futures.stream().allMatch(Future::isDone)) {
                break;
            }

            SleepUtil.sleep(1000);
        }

        //Send friend request
        SkyXploreFriendshipActions.fillSearchCharacterForm(driver1, userData2.getUsername());
        SkyXploreFriendshipActions.findFriendCandidate(driver1, userData2.getUsername())
            .click();

        SentFriendRequest sentFriendRequest = AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getSentFriendRequests(driver1), t -> !t.isEmpty())
            .stream()
            .filter(sentRequest -> sentRequest.getFriendName().equals(userData2.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("SentFriendRequest not found."));

        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingFriendRequest -> incomingFriendRequest.getSenderName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest not found."));

        //Cancel friend request by sender
        sentFriendRequest.cancel();

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getSentFriendRequests(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("SentFriendRequest still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest still present."));

        //Cancel friend request by friend
        SkyXploreFriendshipActions.fillSearchCharacterForm(driver1, userData2.getUsername());
        SkyXploreFriendshipActions.findFriendCandidate(driver1, userData2.getUsername())
            .click();

        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingRequest -> incomingRequest.getSenderName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest not found."))
            .cancel();

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getSentFriendRequests(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("SentFriendRequest still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest still present."));

        //Accept friend request
        SkyXploreFriendshipActions.fillSearchCharacterForm(driver1, userData2.getUsername());
        SkyXploreFriendshipActions.findFriendCandidate(driver1, userData2.getUsername())
            .click();

        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingRequest -> incomingRequest.getSenderName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest not found."))
            .accept();

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getSentFriendRequests(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("SentFriendRequest still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest still present."));

        Friend friend = AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getFriends(driver1), friends -> !friends.isEmpty())
            .stream()
            .filter(fr -> fr.getName().equals(userData2.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friend not found."));

        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getFriends(driver2), friends -> !friends.isEmpty())
            .stream()
            .filter(fr -> fr.getName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friend not found."));

        //Remove friendship by sender
        friend.remove();
        SkyXploreMainMenuActions.confirmFriendDeletion(driver1);

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));

        //Remove friendship by friend
        SkyXploreFriendshipActions.fillSearchCharacterForm(driver1, userData2.getUsername());
        SkyXploreFriendshipActions.findFriendCandidate(driver1, userData2.getUsername())
            .click();
        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingFriendRequest -> incomingFriendRequest.getSenderName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest not found."))
            .accept();

        AwaitilityWrapper.getListWithWait(() -> SkyXploreFriendshipActions.getFriends(driver2), friends -> !friends.isEmpty())
            .stream()
            .filter(fr -> fr.getName().equals(userData1.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friend not found."))
            .remove();

        SkyXploreMainMenuActions.confirmFriendDeletion(driver2);

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));
    }

    private Future<Void> headToMainMenu(WebDriver driver, RegistrationParameters userData) {
        return EXECUTOR_SERVICE.submit(() -> {
            Navigation.toIndexPage(driver);
            IndexPageActions.registerUser(driver, userData);
            ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);
            SkyXploreCharacterActions.submitForm(driver);
            return null;
        });
    }
}
