package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class SolarSystemResponseExtractorTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_SOLAR_SYSTEM_NAME = "default-solar-system-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private VisibilityFacade visibilityFacade;

    @InjectMocks
    private SolarSystemResponseExtractor underTest;

    @Mock
    private Universe universe;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private Planet planet;

    @Mock
    private SolarSystem filteredSolarSystem;

    @Test
    public void getSolarSystems() {
        SolarSystem solarSystem = SolarSystem.builder()
            .solarSystemId(SOLAR_SYSTEM_ID)
            .coordinate(coordinateModel)
            .defaultName(DEFAULT_SOLAR_SYSTEM_NAME)
            .planets(CollectionUtils.singleValueMap(UUID.randomUUID(), planet))
            .customNames(new OptionalHashMap<>())
            .build();
        given(universe.getSystems()).willReturn(CollectionUtils.toMap(new BiWrapper<>(coordinate1, solarSystem), new BiWrapper<>(coordinate2, filteredSolarSystem)));
        given(visibilityFacade.isVisible(USER_ID, filteredSolarSystem)).willReturn(false);
        given(visibilityFacade.isVisible(USER_ID, solarSystem)).willReturn(true);
        given(coordinateModel.getCoordinate()).willReturn(coordinate1);

        List<MapSolarSystemResponse> result = underTest.getSolarSystems(USER_ID, universe);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.get(0).getCoordinate()).isEqualTo(coordinate1);
        assertThat(result.get(0).getPlanetNum()).isEqualTo(1);
        assertThat(result.get(0).getSolarSystemName()).isEqualTo(DEFAULT_SOLAR_SYSTEM_NAME);
    }
}