package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UniverseDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private UniverseConverter converter;

    @Mock
    private UniverseRepository repository;

    @InjectMocks
    private UniverseDao underTest;

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.existsById(GAME_ID_STRING)).willReturn(true);

        underTest.deleteById(GAME_ID);

        verify(repository).deleteById(GAME_ID_STRING);
    }
}