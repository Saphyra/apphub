package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
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
}