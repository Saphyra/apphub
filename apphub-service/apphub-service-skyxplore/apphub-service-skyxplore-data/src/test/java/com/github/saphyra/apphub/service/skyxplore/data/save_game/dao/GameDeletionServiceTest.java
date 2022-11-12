package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String LOCALE = "locale";

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private SkyXploreGameApiClient gameClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private GameDeletionService underTest;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private GameModel gameModel;

    @Test
    public void deleteByUserId() {
        given(playerDao.getByUserId(USER_ID)).willReturn(Arrays.asList(playerModel));
        given(gameDao.getByHost(USER_ID)).willReturn(Arrays.asList(gameModel));
        given(gameModel.getGameId()).willReturn(GAME_ID);
        given(playerModel.getUsername()).willReturn(USERNAME);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        given(gameDao.findByIdValidated(GAME_ID)).willReturn(gameModel);

        underTest.deleteByUserId(USER_ID);

        verify(playerModel).setAi(true);
        verify(playerModel).setUsername(USERNAME + " (AI)");
        verify(playerDao).save(playerModel);

        verify(gameModel).setMarkedForDeletion(true);
        verify(gameModel).setMarkedForDeletionAt(CURRENT_TIME);

        verify(gameDao).save(gameModel);
    }

    @Test
    public void deleteByGameId_gameNotFound() {
        Throwable ex = catchThrowable(() -> underTest.deleteByGameId(GAME_ID, USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND);
    }

    @Test
    public void deleteByGameId_forbiddenOperation() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.of(gameModel));
        given(gameModel.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.deleteByGameId(GAME_ID, USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void deleteByGameId() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.of(gameModel));
        given(gameModel.getHost()).willReturn(USER_ID);
        given(localeProvider.getOrDefault()).willReturn(LOCALE);

        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(gameDao.findByIdValidated(GAME_ID)).willReturn(gameModel);

        underTest.deleteByGameId(GAME_ID, USER_ID);

        verify(gameModel).setMarkedForDeletion(true);
        verify(gameModel).setMarkedForDeletionAt(CURRENT_TIME);

        verify(gameDao).save(gameModel);
        verify(gameClient).deleteGame(GAME_ID, LOCALE);
    }
}