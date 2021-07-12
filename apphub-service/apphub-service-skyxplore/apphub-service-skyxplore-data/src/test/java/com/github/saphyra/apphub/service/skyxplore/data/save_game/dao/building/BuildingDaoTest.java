package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuildingDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String BUILDING_ID_STRING = "building-id";
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String SURFACE_ID_STRING = "surface-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BuildingConverter converter;

    @Mock
    private BuildingRepository repository;

    @InjectMocks
    private BuildingDao underTest;

    @Mock
    private BuildingEntity entity;

    @Mock
    private BuildingModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(BUILDING_ID)).willReturn(BUILDING_ID_STRING);
        given(repository.findById(BUILDING_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<BuildingModel> result = underTest.findById(BUILDING_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void findBySurfaceId() {
        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);
        given(repository.findBySurfaceId(SURFACE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<BuildingModel> result = underTest.findBySurfaceId(SURFACE_ID);

        assertThat(result).contains(model);
    }
}