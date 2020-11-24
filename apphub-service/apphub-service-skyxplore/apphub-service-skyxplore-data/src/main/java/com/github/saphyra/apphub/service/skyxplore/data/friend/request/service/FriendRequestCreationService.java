package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FriendRequestCreationService {
    private final FriendRequestDao friendRequestDao;
    private final CharacterDao characterDao;
    private final FriendRequestFactory friendRequestFactory;

    public void createFriendRequest(UUID senderId, UUID friendId) {
        if (!characterDao.findById(friendId).isPresent()) {
            //TODO throw exception
        }

        if (friendRequestDao.findBySenderIdAndFriendId(senderId, friendId).isPresent()) {
            //TODO throw exception
        }

        //TODO check friendship existence

        FriendRequest friendRequest = friendRequestFactory.create(senderId, friendId);
        friendRequestDao.save(friendRequest);
    }
}
