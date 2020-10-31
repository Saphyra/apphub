package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreCharacterController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreCharacterControllerImpl implements SkyXploreCharacterController {
    @Override
    //TODO unit test
    //TODO api test
    //todo int test
    public boolean isCharacterExistsForUser(AccessTokenHeader accessTokenHeader) {
        return false; //TODO implement
    }
}
