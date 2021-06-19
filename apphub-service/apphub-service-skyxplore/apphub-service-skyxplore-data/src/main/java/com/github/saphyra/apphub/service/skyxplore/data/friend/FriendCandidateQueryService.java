package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
class FriendCandidateQueryService {
    private final CharacterDao characterDao;
    private final SkyXploreCharacterModelConverter characterModelConverter;
    private final FriendRequestDao friendRequestDao;
    private final FriendshipDao friendshipDao;

    List<SkyXploreCharacterModel> getFriendCandidates(UUID userId, String queryString) {
        return characterDao.getByNameLike(queryString)
            .stream()
            .filter(skyXploreCharacter -> !skyXploreCharacter.getUserId().equals(userId))
            .filter(skyXploreCharacter -> !friendRequestDao.findBySenderIdAndFriendId(userId, skyXploreCharacter.getUserId()).isPresent())
            .filter(skyXploreCharacter -> !friendshipDao.findByFriendIds(userId, skyXploreCharacter.getUserId()).isPresent())
            .map(characterModelConverter::convertEntity)
            .collect(Collectors.toList());
    }
}
