package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreFriendDataController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.exception.NotImplementedException;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
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

    @Override
    //TODO unit test
    //TODO int test
    public List<SkyXploreCharacterModel> getFriends(OneParamRequest<String> queryString, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get the characters with name {}", accessTokenHeader.getUserId(), queryString.getValue());
        SkyXploreCharacter ownCharacter = characterDao.findByIdValidated(accessTokenHeader.getUserId());

        return characterDao.getByNameLike(queryString.getValue())
            .stream()
            .filter(skyXploreCharacter -> !skyXploreCharacter.getUserId().equals(ownCharacter.getUserId()))
            .map(characterModelConverter::convertEntity)
            .collect(Collectors.toList());
    }

    @Override
    //TODO unit test
    //TODO int test
    public void addFriend(OneParamRequest<UUID> characterId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} as friend", accessTokenHeader.getUserId(), characterId.getValue());
        throw new NotImplementedException(); //TODO implement
    }
}
