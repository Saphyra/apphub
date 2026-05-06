package com.github.saphyra.apphub.service.feature.skyxplore.data.character;

import com.github.saphyra.apphub.api.feature.skyxplore.data.server.SkyXploreCharacterDataController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.feature.skyxplore.data.character.service.creation.CharacterCreationService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.common.SkyXploreCharacterModelConverter;
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
    public OneParamResponse<String> getCharacterName(AccessToken accessToken) {
        String result = characterDao.findByIdValidated(accessToken.getUserId())
            .getName();
        return new OneParamResponse<>(result);
    }

    @Override
    public void createOrUpdateCharacter(SkyXploreCharacterModel character, AccessToken accessToken) {
        log.info("Creating or updating SkyXplore character for user {}", accessToken.getUserId());
        characterCreationService.create(accessToken.getUserId(), character);
    }

    @Override
    public ResponseEntity<SkyXploreCharacterModel> internalGetCharacterByUserId(UUID userId) {
        log.info("Querying character for userId {}", userId);
        return characterModelConverter.convertEntity(characterDao.findById(userId))
            .map(ResponseEntity::ok)
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public OneParamResponse<Boolean> exists(AccessToken accessToken) {
        log.info("Checking if character exists for user {}", accessToken.getUserId());
        return new OneParamResponse<>(characterDao.exists(accessToken.getUserId()));
    }
}
