package com.github.saphyra.apphub.service.skyxplore.game.service.map;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameMapController;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.SolarSystemConnectionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.UniverseResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MapControllerImpl implements SkyXploreGameMapController {
    private final GameDao gameDao;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public UniverseResponse getMap(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his map.", accessTokenHeader.getUserId());
        Game game = gameDao.findByUserIdValidated(accessTokenHeader.getUserId());
        return toUniverseResponse(game.getUniverse());
    }

    private UniverseResponse toUniverseResponse(Universe universe) {
        List<MapSolarSystemResponse> solarSystems = universe.getSystems()
            .values()
            .stream()
            .map(solarSystem -> MapSolarSystemResponse.builder()
                .solarSystemId(solarSystem.getSolarSystemId())
                .coordinate(solarSystem.getCoordinate())
                .solarSystemName(solarSystem.getDefaultName())
                .planetNum(solarSystem.getPlanets().size())
                .build()
            )
            .collect(Collectors.toList());


        List<SolarSystemConnectionResponse> connections = universe.getConnections()
            .stream()
            .map(connection -> SolarSystemConnectionResponse.builder()
                .a(connection.getLine().getA())
                .b(connection.getLine().getB())
                .build())
            .collect(Collectors.toList());

        return UniverseResponse.builder()
            .universeSize(universe.getSize())
            .solarSystems(solarSystems)
            .connections(connections)
            .build();
    }
}
