package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreCharacterDataController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreCharacterDataControllerImpl implements SkyXploreCharacterDataController {
    @Override
    //TODO unit test
    //TODO api test
    //todo int test
    public boolean isCharacterExistsForUser(AccessTokenHeader accessTokenHeader) {
        log.info("Checking is SkyXplore character is present for user {}", accessTokenHeader.getUserId());
        return false; //TODO implement
    }

    @Override
    public ResponseEntity<SkyXploreCharacterModel> getCharacter(AccessTokenHeader accessTokenHeader) {
        log.info("Querying SkyXplore character for user {}", accessTokenHeader.getUserId());
        //TODO implement
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    //TODO unit test
    //todo int test
    public void createOrUpdateCharacter(SkyXploreCharacterModel character, AccessTokenHeader accessTokenHeader) {
        log.info("Creating or updating SkyXplore character for user {}", accessTokenHeader.getUserId());
        //TODO implement
    }
}
