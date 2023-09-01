package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreUtils;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Friend;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

public class FriendshipCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void friendshipCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        SkyXploreUtils.registerAndNavigateToMainMenu(List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)));

        SentFriendRequest sentFriendRequest = sendFriendRequest(driver1, driver2, userData1, userData2);
        cancelFriendRequestBySender(driver1, driver2, sentFriendRequest);
        cancelFriendRequestByFriend(driver1, driver2, userData1, userData2);
        Friend friend = acceptFriendRequest(driver1, driver2, userData1, userData2);
        removeFriendshipBySender(driver1, driver2, friend);
        removeFriendshipByFriend(driver1, driver2, userData1, userData2);
    }

    private static SentFriendRequest sendFriendRequest(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
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
        return sentFriendRequest;
    }

    private static void cancelFriendRequestBySender(WebDriver driver1, WebDriver driver2, SentFriendRequest sentFriendRequest) {
        sentFriendRequest.cancel();

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getSentFriendRequests(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("SentFriendRequest still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getIncomingFriendRequests(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("IncomingFriendRequest still present."));
    }

    private static void cancelFriendRequestByFriend(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
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
    }

    private static Friend acceptFriendRequest(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
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
        return friend;
    }

    private static void removeFriendshipBySender(WebDriver driver1, WebDriver driver2, Friend friend) {
        friend.remove();
        SkyXploreMainMenuActions.confirmFriendDeletion(driver1);

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver1), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));

        AwaitilityWrapper.getWithWait(() -> SkyXploreFriendshipActions.getFriends(driver2), List::isEmpty)
            .orElseThrow(() -> new RuntimeException("Friend still present."));
    }

    private static void removeFriendshipByFriend(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
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
}
