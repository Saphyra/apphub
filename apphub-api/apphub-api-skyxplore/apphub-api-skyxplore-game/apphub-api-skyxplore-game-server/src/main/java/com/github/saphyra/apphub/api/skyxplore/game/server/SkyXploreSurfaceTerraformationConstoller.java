package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreSurfaceTerraformationConstoller {
    @PostMapping(Endpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE)
    SurfaceResponse terraformSurface(@RequestBody OneParamRequest<String> surfaceType, @PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION)
    SurfaceResponse cancelTerraformation(@PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
