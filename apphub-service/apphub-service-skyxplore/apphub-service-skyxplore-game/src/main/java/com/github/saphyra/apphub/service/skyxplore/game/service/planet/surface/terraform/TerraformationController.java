package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreSurfaceTerraformationController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TerraformationController implements SkyXploreSurfaceTerraformationController {
    private final TerraformationService terraformationService;
    private final CancelTerraformationService cancelTerraformationService;

    @Override
    public SurfaceResponse terraformSurface(OneParamRequest<String> surfaceType, UUID planetId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to terraform surface {} to {} on planet {}", accessTokenHeader.getUserId(), surfaceId, surfaceType.getValue(), planetId);
        return terraformationService.terraform(accessTokenHeader.getUserId(), planetId, surfaceId, surfaceType.getValue());
    }

    @Override
    public SurfaceResponse cancelTerraformation(UUID planetId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel terraformation on planet {} and surface {}", accessTokenHeader.getUserId(), planetId, surfaceId);
        return cancelTerraformationService.cancelTerraformationOfSurface(accessTokenHeader.getUserId(), planetId, surfaceId);
    }
}
