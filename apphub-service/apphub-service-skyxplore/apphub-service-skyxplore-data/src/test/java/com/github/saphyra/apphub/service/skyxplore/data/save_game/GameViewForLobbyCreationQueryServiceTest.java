package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance.AllianceDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameViewForLobbyCreationQueryServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private AllianceDao allianceDao;

    @InjectMocks
    private GameViewForLobbyCreationQueryService underTest;

    @Mock
    private GameModel gameModel;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private PlayerModel aiPlayerModel;

    @Mock
    private AllianceModel allianceModel;

    @BeforeEach
    public void setUp() {
        given(gameDao.findByIdValidated(GAME_ID)).willReturn(gameModel);
    }

    @Test
    public void forbiddenOperation() {
        given(gameModel.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.getView(USER_ID, GAME_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void getView() {
        given(gameModel.getHost()).willReturn(USER_ID);
        given(playerDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(playerModel, aiPlayerModel));
        given(aiPlayerModel.getAi()).willReturn(true);
        given(playerModel.getAi()).willReturn(false);
        given(playerModel.getUserId()).willReturn(USER_ID);
        given(playerModel.getAllianceId()).willReturn(ALLIANCE_ID);
        given(gameModel.getName()).willReturn(GAME_NAME);
        given(allianceDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(allianceModel));

        GameViewForLobbyCreation result = underTest.getView(USER_ID, GAME_ID);

        assertThat(result.getHostAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getName()).isEqualTo(GAME_NAME);
        assertThat(result.getAlliances()).containsExactly(allianceModel);
        assertThat(result.getPlayers()).containsExactly(playerModel);
    }
}