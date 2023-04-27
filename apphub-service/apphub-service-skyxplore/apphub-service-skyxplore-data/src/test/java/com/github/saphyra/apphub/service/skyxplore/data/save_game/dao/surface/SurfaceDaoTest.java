package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SurfaceDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String SURFACE_ID_STRING = "surface-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String PLANET_ID_STRING = "planet-id";
    private static final int PAGE = 135;
    private static final int ITEMS_PER_PAGE = 5743;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SurfaceConverter converter;

    @Mock
    private SurfaceRepository repository;

    @InjectMocks
    private SurfaceDao underTest;

    @Mock
    private SurfaceEntity entity;

    @Mock
    private SurfaceModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);
        given(repository.findById(SURFACE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<SurfaceModel> result = underTest.findById(SURFACE_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByPlanetId() {
        given(uuidConverter.convertDomain(PLANET_ID)).willReturn(PLANET_ID_STRING);
        given(repository.getByPlanetId(PLANET_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<SurfaceModel> result = underTest.getByPlanetId(PLANET_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);
        given(repository.existsById(SURFACE_ID_STRING)).willReturn(true);

        underTest.deleteById(SURFACE_ID);

        verify(repository).deleteById(SURFACE_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}