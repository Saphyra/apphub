package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.data.config.GameDataProperties;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class GameCleanupServiceTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer EXPIRATION_MINUTES = 43;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private GameDao gameDao;

    @Mock
    private GameDataProperties properties;

    @Mock
    private GameItemService gameItemService;

    @Mock
    private SettingDao settingDao;

    private GameCleanupService underTest;

    @Mock
    private GameModel expiredGame;

    @Mock
    private GameModel game;

    @BeforeEach
    public void setUp() {
        underTest = GameCleanupService.builder()
            .dateTimeUtil(dateTimeUtil)
            .gameDao(gameDao)
            .properties(properties)
            .gameItemServices(List.of(gameItemService))
            .settingDao(settingDao)
            .build();
    }

    @Test
    public void deleteMarkedGames() {
        given(gameDao.getGamesMarkedForDeletion()).willReturn(List.of(expiredGame, game));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getGameDeletionExpirationMinutes()).willReturn(EXPIRATION_MINUTES);
        given(expiredGame.getMarkedForDeletionAt()).willReturn(CURRENT_TIME.minusMinutes(EXPIRATION_MINUTES + 1));
        given(expiredGame.getGameId()).willReturn(GAME_ID);
        given(game.getMarkedForDeletionAt()).willReturn(CURRENT_TIME.minusMinutes(EXPIRATION_MINUTES));

        underTest.deleteMarkedGames();

        then(settingDao).should().deleteByGameId(GAME_ID);
        then(gameItemService).should().deleteByGameId(GAME_ID);
        verifyNoMoreInteractions(gameItemService);
    }
}