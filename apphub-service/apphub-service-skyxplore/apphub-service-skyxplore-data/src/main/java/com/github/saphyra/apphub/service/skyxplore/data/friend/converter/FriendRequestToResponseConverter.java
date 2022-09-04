package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestToResponseConverter {
    private final CharacterDao characterDao;

    public SentFriendRequestResponse toSentFriendRequest(FriendRequest friendRequest) {
        return SentFriendRequestResponse.builder()
            .friendRequestId(friendRequest.getFriendRequestId())
            .friendName(characterDao.findByIdValidated(friendRequest.getFriendId()).getName())
            .build();
    }

    public IncomingFriendRequestResponse toIncomingFriendRequest(FriendRequest friendRequest) {
        return IncomingFriendRequestResponse.builder()
            .friendRequestId(friendRequest.getFriendRequestId())
            .senderName(characterDao.findByIdValidated(friendRequest.getSenderId()).getName())
            .build();
    }
}
