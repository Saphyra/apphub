package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.UuidMap;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetConverterTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final UuidStringMap CUSTOM_NAMES = new UuidStringMap(CollectionUtils.singleValueMap(UUID.randomUUID(), "custom-name"));
    private static final Integer SIZE = 3456;
    private static final String PLANET_ID_STRING = "planet-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String SOLAR_SYSTEM_ID_STRING = "solar-system-id";
    private static final String OWNER_STRING = "owner";
    private static final String CUSTOM_NAMES_STRING = "custom-names";
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String BUILDING_ALLOCATIONS = "building-allocations";
    private static final String CITIZEN_ALLOCATIONS = "citizen-allocations";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private PlanetConverter underTest;

    @Test
    public void convertDomain() {
        PlanetModel model = new PlanetModel();
        model.setId(PLANET_ID);
        model.setGameId(GAME_ID);
        model.setSolarSystemId(SOLAR_SYSTEM_ID);
        model.setDefaultName(DEFAULT_NAME);
        model.setCustomNames(CUSTOM_NAMES);
        model.setSize(SIZE);
        model.setOwner(OWNER);
        model.setBuildingAllocations(Collections.emptyMap());
        model.setCitizenAllocations(CollectionUtils.singleValueMap(CITIZEN_ID, PROCESS_ID));

        given(uuidConverter.convertDomain(PLANET_ID)).willReturn(PLANET_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(OWNER)).willReturn(OWNER_STRING);

        given(objectMapperWrapper.writeValueAsString(Collections.emptyMap())).willReturn(BUILDING_ALLOCATIONS);
        given(objectMapperWrapper.writeValueAsString(CollectionUtils.singleValueMap(CITIZEN_ID, PROCESS_ID))).willReturn(CITIZEN_ALLOCATIONS);
        given(objectMapperWrapper.writeValueAsString(CUSTOM_NAMES)).willReturn(CUSTOM_NAMES_STRING);

        PlanetEntity result = underTest.convertDomain(model);

        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID_STRING);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES_STRING);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getOwner()).isEqualTo(OWNER_STRING);
        assertThat(result.getBuildingAllocations()).isEqualTo(BUILDING_ALLOCATIONS);
        assertThat(result.getCitizenAllocations()).isEqualTo(CITIZEN_ALLOCATIONS);
    }

    @Test
    public void convertEntity() {
        PlanetEntity entity = PlanetEntity.builder()
            .planetId(PLANET_ID_STRING)
            .gameId(GAME_ID_STRING)
            .solarSystemId(SOLAR_SYSTEM_ID_STRING)
            .defaultName(DEFAULT_NAME)
            .customNames(CUSTOM_NAMES_STRING)
            .size(SIZE)
            .owner(OWNER_STRING)
            .buildingAllocations(BUILDING_ALLOCATIONS)
            .citizenAllocations(CITIZEN_ALLOCATIONS)
            .build();

        given(uuidConverter.convertEntity(PLANET_ID_STRING)).willReturn(PLANET_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(SOLAR_SYSTEM_ID_STRING)).willReturn(SOLAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(OWNER_STRING)).willReturn(OWNER);

        //noinspection unchecked
        given(objectMapperWrapper.readValue(eq(BUILDING_ALLOCATIONS), any(TypeReference.class))).willReturn(Collections.emptyMap());
        given(objectMapperWrapper.readValue(CITIZEN_ALLOCATIONS, UuidMap.class)).willReturn(new UuidMap(CollectionUtils.singleValueMap(CITIZEN_ID, PROCESS_ID)));
        given(objectMapperWrapper.readValue(CUSTOM_NAMES_STRING, UuidStringMap.class)).willReturn(CUSTOM_NAMES);

        PlanetModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(PLANET_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PLANET);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getOwner()).isEqualTo(OWNER);
        assertThat(result.getBuildingAllocations()).isEqualTo(Collections.emptyMap());
        assertThat(result.getCitizenAllocations()).containsEntry(CITIZEN_ID, PROCESS_ID);
    }
}