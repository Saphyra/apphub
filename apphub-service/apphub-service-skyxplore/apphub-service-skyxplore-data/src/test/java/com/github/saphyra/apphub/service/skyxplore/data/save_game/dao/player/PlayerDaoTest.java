package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
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
public class PlayerDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final String USER_ID_STRING = "user-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_ID_STRING = "player-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private PlayerConverter converter;

    @Mock
    private PlayerRepository repository;

    @InjectMocks
    private PlayerDao underTest;

    @Mock
    private PlayerModel model;

    @Mock
    private PlayerEntity entity;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void getByUserId() {
        given(repository.getByUserId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<PlayerModel> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void getByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<PlayerModel> result = underTest.getByGameId(GAME_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(PLAYER_ID)).willReturn(PLAYER_ID_STRING);
        given(repository.findById(PLAYER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<PlayerModel> result = underTest.findById(PLAYER_ID);

        assertThat(result).contains(model);
    }
}