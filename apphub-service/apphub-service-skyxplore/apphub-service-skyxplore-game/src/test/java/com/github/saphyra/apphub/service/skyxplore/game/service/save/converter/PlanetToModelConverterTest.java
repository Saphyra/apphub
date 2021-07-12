package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetToModelConverterTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CUSTOM_NAME = "custom-name";
    public static final OptionalHashMap<UUID, String> CUSTOM_NAMES = new OptionalHashMap<>(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME));
    private static final Coordinate COORDINATE = new Coordinate(0, 0);
    private static final int SIZE = 23;
    private static final Integer PRIORITY = 245;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();

    @Mock
    private SurfaceToModelConverter surfaceConverter;

    @Mock
    private CitizenToModelConverter citizenConverter;

    @Mock
    private StorageDetailsToModelConverter storageDetailsConverter;

    @Mock
    private PriorityToModelConverter priorityConverter;

    @InjectMocks
    private PlanetToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceModel surfaceModel;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private GameItem storageDetailsItem;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private PriorityModel priorityModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void convertDeep() {
        Planet planet = Planet.builder()
            .planetId(PLANET_ID)
            .solarSystemId(SOLAR_SYSTEM_ID)
            .defaultName(DEFAULT_NAME)
            .customNames(CUSTOM_NAMES)
            .coordinate(coordinateModel)
            .size(SIZE)
            .population(new OptionalHashMap<>(CollectionUtils.singleValueMap(UUID.randomUUID(), citizen)))
            .surfaces(CollectionUtils.singleValueMap(COORDINATE, surface))
            .storageDetails(storageDetails)
            .priorities(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, PRIORITY))
            .owner(OWNER)
            .build();

        given(game.getGameId()).willReturn(GAME_ID);
        given(surfaceConverter.convertDeep(any(), eq(game))).willReturn(Arrays.asList(surfaceModel));
        given(citizenConverter.convertDeep(any(), eq(game))).willReturn(Arrays.asList(citizenModel));
        given(storageDetailsConverter.convertDeep(storageDetails, game, PLANET_ID, LocationType.PLANET)).willReturn(Arrays.asList(storageDetailsItem));
        given(priorityConverter.convert(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, PRIORITY), PLANET_ID, LocationType.PLANET, game)).willReturn(Arrays.asList(priorityModel));

        List<GameItem> result = underTest.convertDeep(Arrays.asList(planet), game);

        PlanetModel expected = new PlanetModel();
        expected.setId(PLANET_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.PLANET);
        expected.setSolarSystemId(SOLAR_SYSTEM_ID);
        expected.setDefaultName(DEFAULT_NAME);
        expected.setCustomNames(CUSTOM_NAMES);
        expected.setSize(SIZE);
        expected.setOwner(OWNER);

        assertThat(result).containsExactlyInAnyOrder(expected, surfaceModel, citizenModel, storageDetailsItem, priorityModel, coordinateModel);
    }
}