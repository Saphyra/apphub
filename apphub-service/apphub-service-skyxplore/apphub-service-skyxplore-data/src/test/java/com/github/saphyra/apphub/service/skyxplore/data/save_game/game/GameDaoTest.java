package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
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
public class GameDaoTest {
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private GameConverter converter;

    @Mock
    private GameRepository repository;

    @InjectMocks
    private GameDao underTest;

    @Mock
    private GameEntity entity;

    @Mock
    private GameModel model;

    @Test
    public void deleteById() {
        given(repository.existsById(GAME_ID_STRING)).willReturn(true);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteById(GAME_ID);

        verify(repository).deleteById(GAME_ID_STRING);
    }

    @Test
    public void getByHost() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByHost(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<GameModel> result = underTest.getByHost(USER_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.findById(GAME_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<GameModel> result = underTest.findById(GAME_ID);

        assertThat(result).contains(model);
    }
}