package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlanetDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SOLAR_SYSTEM_ID_STRING = "solar-system-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String PLANET_ID_STRING = "planet-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private PlanetConverter converter;

    @Mock
    private PlanetRepository repository;

    @InjectMocks
    private PlanetDao underTest;

    @Mock
    private PlanetEntity entity;

    @Mock
    private PlanetModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(PLANET_ID)).willReturn(PLANET_ID_STRING);
        given(repository.findById(PLANET_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<PlanetModel> result = underTest.findById(PLANET_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getBySolarSystemId() {
        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(repository.getBySolarSystemId(SOLAR_SYSTEM_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<PlanetModel> result = underTest.getBySolarSystemId(SOLAR_SYSTEM_ID);

        assertThat(result).containsExactly(model);
    }
}