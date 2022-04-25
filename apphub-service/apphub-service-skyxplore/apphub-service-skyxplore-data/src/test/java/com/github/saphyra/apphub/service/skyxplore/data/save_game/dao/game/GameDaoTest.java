package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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

    @Test
    public void findByIdValidated() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(GAME_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND);
    }

    @Test
    public void getGamesMarkedForDeletion() {
        given(repository.getGamesMarkedForDeletion()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        List<GameModel> result = underTest.getGamesMarkedForDeletion();

        assertThat(result).containsExactly(model);
    }
}