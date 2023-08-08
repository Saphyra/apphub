package com.github.saphyra.apphub.integraton.frontend.community;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.frontend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequest;
import com.github.saphyra.apphub.integration.structure.api.community.Friendship;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipCrudTest extends SeleniumTest {
    @Test(groups = "community")
    public void friendRequestCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);

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

        //Search - User not found
        FriendRequestActions.fillSearchForm(driver1, UUID.randomUUID().toString());

        FriendRequestActions.verifyUserNotFound(driver1);

        //Search - Query too short
        FriendRequestActions.fillSearchForm(driver1, "as");

        FriendRequestActions.verifyQueryTooShort(driver1);

        //Send friend request
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

        //Cancel friend request by sender
        friendRequest.delete();

        NotificationUtil.verifySuccessNotification(driver1, "Barátkérelem törölve.");

        assertThat(FriendRequestActions.getSentFriendRequests(driver1)).isEmpty();

        CommunityActions.openFriendsTab(driver2);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendRequestActions.getReceivedFriendRequests(driver2).isEmpty())
            .assertTrue("FriendRequest not deleted.");

        //Cancel friend request by receiver
        FriendRequestActions.sendFriendRequest(driver1, userData2.getUsername());

        CommunityActions.openFriendsTab(driver2);
        receivedFriendRequests = AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(driver2), fr -> !fr.isEmpty());

        receivedFriendRequests.get(0).delete();

        NotificationUtil.verifySuccessNotification(driver2, "Barátkérelem törölve.");
        assertThat(FriendRequestActions.getReceivedFriendRequests(driver2)).isEmpty();

        CommunityActions.openFriendsTab(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendRequestActions.getSentFriendRequests(driver1).isEmpty())
            .assertTrue("FriendRequest not deleted.");

        //Accept friend request
        FriendRequestActions.sendFriendRequest(driver1, userData2.getUsername());
        CommunityActions.openFriendsTab(driver2);
        receivedFriendRequests = AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(driver2), fr -> !fr.isEmpty());

        receivedFriendRequests.get(0).accept();

        NotificationUtil.verifySuccessNotification(driver2, "Barátkérelem elfogadva.");
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

        //Remove friendship by sender
        friendship.delete();
        CommonPageActions.confirmConfirmationDialog(driver1, "delete-friendship-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver1, "Barátság megszakítva.");

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver1).isEmpty())
            .assertTrue("Friendship is not deleted.");

        CommunityActions.openFriendsTab(driver2);

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver2).isEmpty())
            .assertTrue("Friendship is not deleted.");

        //Remove friendship by friend
        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData2.getUsername(), driver2);
        CommunityActions.openFriendsTab(driver1);

        AwaitilityWrapper.getListWithWait(() -> FriendshipActions.getFriendships(driver2), friendships -> !friendships.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found."))
            .delete();
        CommonPageActions.confirmConfirmationDialog(driver2, "delete-friendship-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver2, "Barátság megszakítva.");

        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver2).isEmpty())
            .assertTrue("Friendship is not deleted.");

        CommunityActions.openFriendsTab(driver1);
        AwaitilityWrapper.createDefault()
            .until(() -> FriendshipActions.getFriendships(driver1).isEmpty())
            .assertTrue("Friendship is not deleted.");
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
