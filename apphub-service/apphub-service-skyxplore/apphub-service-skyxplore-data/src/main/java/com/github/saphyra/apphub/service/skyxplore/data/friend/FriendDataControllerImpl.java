package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreFriendDataController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestCancelService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.service.FriendRequestQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FriendDataControllerImpl implements SkyXploreFriendDataController {
    private final CharacterDao characterDao;
    private final SkyXploreCharacterModelConverter characterModelConverter;
    private final FriendRequestCreationService friendRequestCreationService;
    private final FriendRequestQueryService friendRequestQueryService;
    private final FriendRequestCancelService friendRequestCancelService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public List<SkyXploreCharacterModel> getFriendCandidates(OneParamRequest<String> queryString, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get the characters with name {}", accessTokenHeader.getUserId(), queryString.getValue());
        SkyXploreCharacter ownCharacter = characterDao.findByIdValidated(accessTokenHeader.getUserId());

        return characterDao.getByNameLike(queryString.getValue())
            .stream()
            .filter(skyXploreCharacter -> !skyXploreCharacter.getUserId().equals(ownCharacter.getUserId()))
            //TODO filter friends
            //TODO filter friend requests
            .map(characterModelConverter::convertEntity)
            .collect(Collectors.toList());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void createFriendRequest(OneParamRequest<UUID> characterId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} as friend", accessTokenHeader.getUserId(), characterId.getValue());
        friendRequestCreationService.createFriendRequest(accessTokenHeader.getUserId(), characterId.getValue());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public List<SentFriendRequestResponse> getSentFriendRequests(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query his sent friend requests", accessTokenHeader.getUserId());
        return friendRequestQueryService.getSentFriendRequests(accessTokenHeader.getUserId());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void cancelFriendRequest(UUID friendRequestId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel friendRequest {}", accessTokenHeader.getUserId(), friendRequestId);
        friendRequestCancelService.cancelFriendRequest(accessTokenHeader.getUserId(), friendRequestId);
    }
}
