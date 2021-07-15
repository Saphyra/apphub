package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CUSTOM_NAME = "custom-name";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer RADIUS = 23453;

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private CoordinateLoader coordinateLoader;

    @Mock
    private PlanetLoader planetLoader;

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeanFactory.create(Executors.newSingleThreadExecutor());

    @InjectMocks
    private SolarSystemLoader underTest;

    @Mock
    private SolarSystemModel solarSystemModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planet planet;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(GAME_ID, GameItemType.SOLAR_SYSTEM, SolarSystemModel[].class)).willReturn(Arrays.asList(solarSystemModel));

        given(solarSystemModel.getId()).willReturn(SOLAR_SYSTEM_ID);
        given(solarSystemModel.getRadius()).willReturn(RADIUS);
        given(solarSystemModel.getDefaultName()).willReturn(DEFAULT_NAME);
        given(solarSystemModel.getCustomNames()).willReturn(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME));

        given(coordinateLoader.loadOneByReferenceId(SOLAR_SYSTEM_ID)).willReturn(coordinateModel);
        given(planetLoader.load(SOLAR_SYSTEM_ID)).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));

        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        Map<Coordinate, SolarSystem> result = underTest.load(GAME_ID);

        assertThat(result).hasSize(1);
        SolarSystem solarSystem = result.get(coordinate);
        assertThat(solarSystem.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(solarSystem.getRadius()).isEqualTo(RADIUS);
        assertThat(solarSystem.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(solarSystem.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
        assertThat(solarSystem.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(solarSystem.getPlanets()).containsEntry(PLANET_ID, planet);
    }
}