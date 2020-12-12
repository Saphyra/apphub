package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.api.user.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.model.response.LastVisitedPageResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.data.friend.FriendshipProperties;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ActiveFriendsQueryService {
    private final DateTimeUtil dateTimeUtil;
    private final FriendshipDao friendshipDao;
    private final FriendNameQueryService friendNameQueryService;
    private final FriendIdExtractor friendIdExtractor;
    private final UserAuthenticationApiClient authenticationClient;
    private final FriendshipProperties friendshipProperties;
    private final LocaleProvider localeProvider;

    public List<ActiveFriendResponse> getActiveFriends(UUID userId) {
        return friendshipDao.getByFriendId(userId)
            .stream()
            .filter(friendship -> isActive(userId, friendship))
            //TODO filter friends already in lobby
            .map(friendship -> ActiveFriendResponse.builder()
                .friendName(friendNameQueryService.getFriendName(friendship, userId))
                .friendId(friendIdExtractor.getFriendId(friendship, userId))
                .build())
            .collect(Collectors.toList());
    }

    private boolean isActive(UUID userId, Friendship friendship) {
        UUID friendId = friendIdExtractor.getFriendId(friendship, userId);
        LastVisitedPageResponse lastVisitedPageResponse = authenticationClient.getLastVisitedPage(friendId, localeProvider.getLocaleValidated()); //TODO get data from connected users
        log.info("Checking if friend {} is active by lastVisitedPage {}", friendId, lastVisitedPageResponse);

        if (!lastVisitedPageResponse.getPageUrl().equals(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)) {
            log.info("LastVisitedPage is not {}", Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
            return false;
        }

        LocalDateTime actualDate = dateTimeUtil.getCurrentDate();
        boolean isRecentlyActive = actualDate.minusMinutes(friendshipProperties.getActiveTimeoutMinutes()).isBefore(lastVisitedPageResponse.getLastAccess());
        log.info("{} opened the SkyXplore start page at {}, it considered as active: {}", friendId, lastVisitedPageResponse.getLastAccess(), isRecentlyActive);
        return isRecentlyActive;
    }
}
