package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreCharacterDataController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.service.creation.CharacterCreationService;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterDataControllerImpl implements SkyXploreCharacterDataController {
    private final CharacterCreationService characterCreationService;
    private final CharacterDao characterDao;
    private final SkyXploreCharacterModelConverter characterModelConverter;

    @Override
    //TODO unit test
    //todo int test
    public boolean isCharacterExistsForUser(AccessTokenHeader accessTokenHeader) {
        log.info("Checking is SkyXplore character is present for user {}", accessTokenHeader.getUserId());
        return characterDao.exists(accessTokenHeader.getUserId());
    }

    @Override
    //TODO unit test
    //todo int test
    public ResponseEntity<SkyXploreCharacterModel> getCharacter(AccessTokenHeader accessTokenHeader) {
        log.info("Querying SkyXplore character for user {}", accessTokenHeader.getUserId());
        return characterDao.findById(accessTokenHeader.getUserId())
            .map(characterModelConverter::convertEntity)
            .map(ResponseEntity::ok)
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    //TODO unit test
    //todo int test
    public void createOrUpdateCharacter(SkyXploreCharacterModel character, AccessTokenHeader accessTokenHeader) {
        log.info("Creating or updating SkyXplore character for user {}", accessTokenHeader.getUserId());
        characterCreationService.create(accessTokenHeader.getUserId(), character);
    }
}
