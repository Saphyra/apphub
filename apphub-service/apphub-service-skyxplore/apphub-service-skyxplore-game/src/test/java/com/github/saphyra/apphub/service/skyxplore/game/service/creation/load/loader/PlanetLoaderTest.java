package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetLoaderTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CUSTOM_NAME = "custom-name";
    private static final Integer SIZE = 453;
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 35;

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private CoordinateLoader coordinateLoader;

    @Mock
    private SurfaceLoader surfaceLoader;

    @Mock
    private CitizenLoader citizenLoader;

    @Mock
    private PriorityLoader priorityLoader;

    @Mock
    private StorageDetailsLoader storageDetailsLoader;

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @InjectMocks
    private PlanetLoader underTest;

    @Mock
    private PlanetModel planetModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Surface surface;

    @Mock
    private Citizen citizen;

    @Mock
    private StorageDetails storageDetails;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(SOLAR_SYSTEM_ID, GameItemType.PLANET, PlanetModel[].class)).willReturn(Arrays.asList(planetModel));

        given(planetModel.getId()).willReturn(PLANET_ID);
        given(planetModel.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(planetModel.getDefaultName()).willReturn(DEFAULT_NAME);
        given(planetModel.getCustomNames()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME)));
        given(planetModel.getSize()).willReturn(SIZE);
        given(planetModel.getOwner()).willReturn(OWNER);

        given(coordinateLoader.loadOneByReferenceId(PLANET_ID)).willReturn(coordinateModel);
        given(surfaceLoader.load(PLANET_ID)).willReturn(CollectionUtils.singleValueMap(coordinate, surface));
        given(citizenLoader.load(PLANET_ID)).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(CITIZEN_ID, citizen)));
        given(storageDetailsLoader.load(PLANET_ID)).willReturn(storageDetails);
        given(priorityLoader.load(PLANET_ID)).willReturn(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, PRIORITY));

        Map<UUID, Planet> result = underTest.load(SOLAR_SYSTEM_ID);

        assertThat(result).hasSize(1);
        Planet planet = result.get(PLANET_ID);
        assertThat(planet.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(planet.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(planet.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(planet.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
        assertThat(planet.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(planet.getSize()).isEqualTo(SIZE);
        assertThat(planet.getSurfaces()).containsEntry(coordinate, surface);
        assertThat(planet.getOwner()).isEqualTo(OWNER);
        assertThat(planet.getPopulation()).containsEntry(CITIZEN_ID, citizen);
        assertThat(planet.getStorageDetails()).isEqualTo(storageDetails);
        assertThat(planet.getPriorities()).containsEntry(PriorityType.CONSTRUCTION, PRIORITY);
    }
}