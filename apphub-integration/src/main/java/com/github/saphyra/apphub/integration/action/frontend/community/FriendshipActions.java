package com.github.saphyra.apphub.integration.action.frontend.community;

import com.github.saphyra.apphub.integration.structure.api.community.Friendship;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class FriendshipActions {
    public static List<Friendship> getFriendships(WebDriver driver) {
        return CommunityPage.friendships(driver)
            .stream()
            .map(Friendship::new)
            .collect(Collectors.toList());
    }
}
