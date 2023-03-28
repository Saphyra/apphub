package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceResponseQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlanetOverviewControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Mock
    private PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;

    @Mock
    private PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    @Mock
    private SurfaceResponseQueryService surfaceResponseQueryService;

    @Mock
    private PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    @Mock
    private PlanetOverviewQueryService planetOverviewQueryService;

    @Mock
    private RenamePlanetService renamePlanetService;

    @InjectMocks
    private PlanetOverviewControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private PlanetStorageResponse planetStorageResponse;

    @Mock
    private PlanetPopulationOverviewResponse planetPopulationOverviewResponse;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Mock
    private PlanetOverviewResponse planetOverviewResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getSurfaceOfPlanet() {
        given(surfaceResponseQueryService.getSurfaceOfPlanet(USER_ID, PLANET_ID)).willReturn(Arrays.asList(surfaceResponse));

        List<SurfaceResponse> result = underTest.getSurfaceOfPlanet(PLANET_ID, accessTokenHeader);

        assertThat(result).containsExactly(surfaceResponse);
    }

    @Test
    public void getStorageOfPlanet() {
        given(planetStorageOverviewQueryService.getStorage(USER_ID, PLANET_ID)).willReturn(planetStorageResponse);

        PlanetStorageResponse result = underTest.getStorageOfPlanet(PLANET_ID, accessTokenHeader);

        assertThat(result).isEqualTo(planetStorageResponse);
    }

    @Test
    public void getPopulationOverview() {
        given(planetPopulationOverviewQueryService.getPopulationOverview(USER_ID, PLANET_ID)).willReturn(planetPopulationOverviewResponse);

        PlanetPopulationOverviewResponse result = underTest.getPopulationOverview(PLANET_ID, accessTokenHeader);

        assertThat(result).isEqualTo(planetPopulationOverviewResponse);
    }

    @Test
    public void getBuildingOverview() {
        given(planetBuildingOverviewQueryService.getBuildingOverview(USER_ID, PLANET_ID)).willReturn(CollectionUtils.singleValueMap(SurfaceType.CONCRETE.name(), planetBuildingOverviewResponse));

        Map<String, PlanetBuildingOverviewResponse> result = underTest.getBuildingOverview(PLANET_ID, accessTokenHeader);

        assertThat(result).containsEntry(SurfaceType.CONCRETE.name(), planetBuildingOverviewResponse);
    }

    @Test
    public void getOverview() {
        given(planetOverviewQueryService.getOverview(USER_ID, PLANET_ID)).willReturn(planetOverviewResponse);

        PlanetOverviewResponse result = underTest.getPlanetOverview(PLANET_ID, accessTokenHeader);

        assertThat(result).isEqualTo(planetOverviewResponse);
    }

    @Test
    public void renamePlanet() {
        underTest.renamePlanet(new OneParamRequest<>(NEW_PLANET_NAME), PLANET_ID, accessTokenHeader);

        verify(renamePlanetService).rename(USER_ID, PLANET_ID, NEW_PLANET_NAME);
    }
}