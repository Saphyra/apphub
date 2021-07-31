package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
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
public class SolarSystemDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String SOLAR_SYSTEM_ID_STRING = "solar-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SolarSystemConverter converter;

    @Mock
    private SolarSystemRepository repository;

    @InjectMocks
    private SolarSystemDao underTest;

    @Mock
    private SolarSystemEntity entity;

    @Mock
    private SolarSystemModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(repository.findById(SOLAR_SYSTEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<SolarSystemModel> result = underTest.findById(SOLAR_SYSTEM_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<SolarSystemModel> result = underTest.getByGameId(GAME_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(repository.existsById(SOLAR_SYSTEM_ID_STRING)).willReturn(true);

        underTest.deleteById(SOLAR_SYSTEM_ID);

        verify(repository).deleteById(SOLAR_SYSTEM_ID_STRING);
    }
}