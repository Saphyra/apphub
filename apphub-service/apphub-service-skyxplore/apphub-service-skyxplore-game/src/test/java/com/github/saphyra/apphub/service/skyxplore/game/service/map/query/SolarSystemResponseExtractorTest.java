package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class SolarSystemResponseExtractorTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_SOLAR_SYSTEM_NAME = "default-solar-system-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private SolarSystemResponseExtractor underTest;

    @Mock
    private Universe universe;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planet planet;

    @Test
    public void getSolarSystems() {
        SolarSystem solarSystem = SolarSystem.builder()
            .solarSystemId(SOLAR_SYSTEM_ID)
            .coordinate(coordinateModel)
            .defaultName(DEFAULT_SOLAR_SYSTEM_NAME)
            .planets(CollectionUtils.singleValueMap(UUID.randomUUID(), planet))
            .customNames(new OptionalHashMap<>())
            .build();
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(coordinate, solarSystem));
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        List<MapSolarSystemResponse> result = underTest.getSolarSystems(USER_ID, universe);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.get(0).getCoordinate()).isEqualTo(coordinate);
        assertThat(result.get(0).getPlanetNum()).isEqualTo(1);
        assertThat(result.get(0).getSolarSystemName()).isEqualTo(DEFAULT_SOLAR_SYSTEM_NAME);
    }
}