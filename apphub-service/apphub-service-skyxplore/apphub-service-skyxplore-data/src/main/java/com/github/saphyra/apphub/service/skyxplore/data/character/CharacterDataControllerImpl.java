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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterDataControllerImpl implements SkyXploreCharacterDataController {
    private final CharacterCreationService characterCreationService;
    private final CharacterDao characterDao;
    private final SkyXploreCharacterModelConverter characterModelConverter;

    @Override
    public ResponseEntity<SkyXploreCharacterModel> getCharacter(AccessTokenHeader accessTokenHeader) {
        return internalGetCharacterByUserId(accessTokenHeader.getUserId());
    }

    @Override
    public void createOrUpdateCharacter(SkyXploreCharacterModel character, AccessTokenHeader accessTokenHeader) {
        log.info("Creating or updating SkyXplore character for user {}", accessTokenHeader.getUserId());
        characterCreationService.create(accessTokenHeader.getUserId(), character);
    }

    @Override
    public ResponseEntity<SkyXploreCharacterModel> internalGetCharacterByUserId(UUID userId) {
        log.info("Querying character for userId {}", userId);
        return characterModelConverter.convertEntity(characterDao.findById(userId))
            .map(ResponseEntity::ok)
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean exists(UUID userId) {
        log.info("Checking if character exists for user {}", userId);
        return characterDao.exists(userId);
    }
}
