package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendNameQueryService {
    private final CharacterDao characterDao;

    String getFriendName(Friendship friendship, UUID userId) {
        return characterDao.findByIdValidated(friendship.getOtherId(userId)).getName();
    }
}
