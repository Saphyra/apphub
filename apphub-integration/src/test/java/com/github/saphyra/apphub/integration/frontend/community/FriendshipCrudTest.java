package com.github.saphyra.apphub.integration.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.frontend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequest;
import com.github.saphyra.apphub.integration.structure.api.community.Friendship;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "community"})
    public void friendRequestCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();

        Integer serverPort = getServerPort();
        RegistrationUtils.registerUsers(
            serverPort,
            List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2)),
            (driver, registrationParameters) -> ModulesPageActions.openModule(serverPort, driver, ModuleLocation.COMMUNITY)
        );

        search_userNotFound(driver1);
        search_queryTooShort(driver1);
        FriendRequest friendRequest = sendFriendRequest(driver1, driver2, userData1, userData2);
        cancelFriendRequestBySender(driver1, driver2, friendRequest);
        cancelFriendRequestByReceiver(driver1, driver2, userData2);
        Friendship friendship = acceptFriendRequest(driver1, driver2, userData1, userData2);
        removeFriendshipBySender(driver1, driver2, friendship);
        removeFriendshipByFriend(driver1, driver2, userData1, userData2);
    }

    private static void search_userNotFound(WebDriver driver1) {
        FriendRequestActions.fillSearchForm(driver1, UUID.randomUUID().toString());

        FriendRequestActions.verifyUserNotFound(driver1);
    }

    private static void search_queryTooShort(WebDriver driver1) {
        FriendRequestActions.fillSearchForm(driver1, "as");

        FriendRequestActions.verifyQueryTooShort(driver1);
    }

    private static FriendRequest sendFriendRequest(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
        FriendRequestActions.sendFriendRequest(driver1, userData2.getUsername());

        List<FriendRequest> sentFriendRequests = FriendRequestActions.getSentFriendRequests(driver1);

        assertThat(sentFriendRequests).hasSize(1);
        FriendRequest friendRequest = sentFriendRequests.get(0);
        assertThat(friendRequest.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(friendRequest.getEmail()).isEqualTo(userData2.getEmail());

        CommunityActions.openFriendsTab(driver2);
        List<FriendRequest> receivedFriendRequests = AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(driver2), fr -> !fr.isEmpty());

        assertThat(receivedFriendRequests).hasSize(1);
        assertThat(receivedFriendRequests.get(0).getUsername()).isEqualTo(userData1.getUsername());
        assertThat(receivedFriendRequests.get(0).getEmail()).isEqualTo(userData1.getEmail());
        return friendRequest;
    }

    private static void cancelFriendRequestBySender(WebDriver driver1, WebDriver driver2, FriendRequest friendRequest) {
        friendRequest.delete();

        NotificationUtil.verifySuccessNotification(driver1, "Friend request deleted.");

        assertThat(FriendRequestActions.getSentFriendRequests(driver1)).isEmpty();

        CommunityActions.openFriendsTab(driver2);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendRequestActions.getReceivedFriendRequests(driver2).isEmpty())
            .assertTrue("FriendRequest not deleted.");
    }

    private static void cancelFriendRequestByReceiver(WebDriver driver1, WebDriver driver2, RegistrationParameters userData2) {
        List<FriendRequest> receivedFriendRequests;
        FriendRequestActions.sendFriendRequest(driver1, userData2.getUsername());

        CommunityActions.openFriendsTab(driver2);
        receivedFriendRequests = AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(driver2), fr -> !fr.isEmpty());

        receivedFriendRequests.get(0).delete();

        NotificationUtil.verifySuccessNotification(driver2, "Friend request deleted.");
        assertThat(FriendRequestActions.getReceivedFriendRequests(driver2)).isEmpty();

        CommunityActions.openFriendsTab(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendRequestActions.getSentFriendRequests(driver1).isEmpty())
            .assertTrue("FriendRequest not deleted.");
    }

    private static Friendship acceptFriendRequest(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
        List<FriendRequest> receivedFriendRequests;
        FriendRequestActions.sendFriendRequest(driver1, userData2.getUsername());
        CommunityActions.openFriendsTab(driver2);
        receivedFriendRequests = AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(driver2), fr -> !fr.isEmpty());

        receivedFriendRequests.get(0).accept();

        NotificationUtil.verifySuccessNotification(driver2, "Friend request accepted.");
        assertThat(FriendRequestActions.getReceivedFriendRequests(driver2)).isEmpty();

        List<Friendship> receiverFriendships = FriendshipActions.getFriendships(driver2);

        assertThat(receiverFriendships).hasSize(1);
        assertThat(receiverFriendships.get(0).getUsername()).isEqualTo(userData1.getUsername());
        assertThat(receiverFriendships.get(0).getEmail()).isEqualTo(userData1.getEmail());

        CommunityActions.openFriendsTab(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendRequestActions.getSentFriendRequests(driver1).isEmpty())
            .assertTrue("FriendRequest did not disappear.");

        List<Friendship> senderFriendship = FriendshipActions.getFriendships(driver1);
        assertThat(senderFriendship).hasSize(1);
        Friendship friendship = senderFriendship.get(0);
        assertThat(friendship.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(friendship.getEmail()).isEqualTo(userData2.getEmail());
        return friendship;
    }

    private static void removeFriendshipBySender(WebDriver driver1, WebDriver driver2, Friendship friendship) {
        friendship.delete();
        CommonPageActions.confirmConfirmationDialog(driver1, "delete-friendship-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver1, "Friendship ended.");

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver1).isEmpty())
            .assertTrue("Friendship is not deleted.");

        CommunityActions.openFriendsTab(driver2);

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver2).isEmpty())
            .assertTrue("Friendship is not deleted.");
    }

    private static void removeFriendshipByFriend(WebDriver driver1, WebDriver driver2, RegistrationParameters userData1, RegistrationParameters userData2) {
        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData2.getUsername(), driver2);
        CommunityActions.openFriendsTab(driver1);

        AwaitilityWrapper.getListWithWait(() -> FriendshipActions.getFriendships(driver2), friendships -> !friendships.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found."))
            .delete();
        CommonPageActions.confirmConfirmationDialog(driver2, "delete-friendship-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver2, "Friendship ended.");

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver2).isEmpty())
            .assertTrue("Friendship is not deleted.");

        CommunityActions.openFriendsTab(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver1).isEmpty())
            .assertTrue("Friendship is not deleted.");
    }
}
