package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.SleepUtil;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Friend;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.SentFriendRequest;
import com.github.saphyra.apphub.integration.frontend.service.common.CommonPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu.SkyXploreFriendshipActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendshipCrudTest extends SeleniumTest {
    @Test
    public void friendshipCrud() {
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

        //Send friend request
        SkyXploreFriendshipActions.fillSearchCharacterForm(driver1, userData2.getUsername());
        SkyXploreFriendshipActions.findFriendCandidate(driver1, userData2.getUsername())
            .click();
        NotificationUtil.verifySuccessNotification(driver1, "Barátkérelem elküldve.");

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

        NotificationUtil.verifySuccessNotification(driver1, "Barátkérelem visszavonva.");

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

        NotificationUtil.verifySuccessNotification(driver2, "Barátkérelem visszavonva.");

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

        NotificationUtil.verifySuccessNotification(driver2, "Barátkérelem elfogadva.");

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
        CommonPageActions.confirmConfirmationDialog(driver1, "remove-friend-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver1, "Barát eltávolítva.");

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

        CommonPageActions.confirmConfirmationDialog(driver2, "remove-friend-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver2, "Barát eltávolítva.");

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
