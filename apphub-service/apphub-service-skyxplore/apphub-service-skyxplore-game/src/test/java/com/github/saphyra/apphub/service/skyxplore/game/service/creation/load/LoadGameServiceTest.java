package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.GameLoader;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private GameLoader gameLoader;

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBeanFactory executorServiceBeanFactory = ExecutorServiceBeenTestUtils.createFactory(Mockito.mock(ErrorReporterService.class));

    @Mock
    private LoadGameRequestValidator requestValidator;

    @InjectMocks
    private LoadGameService underTest;

    @Mock
    private SkyXploreLoadGameRequest request;

    @Mock
    private GameModel gameModel;


    @AfterEach
    public void verif() {
        verify(requestValidator).validate(request);
    }

    @Test
    public void gameNotFound() {
        given(request.getGameId()).willReturn(GAME_ID);

        Throwable ex = catchThrowable(() -> underTest.loadGame(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND);
    }

    @Test
    public void differentHost() {
        given(request.getGameId()).willReturn(GAME_ID);
        given(request.getHost()).willReturn(HOST);
        given(gameItemLoader.loadItem(GAME_ID, GameItemType.GAME)).willReturn(Optional.of(gameModel));
        given(gameModel.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.loadGame(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void loadGame() {
        given(request.getGameId()).willReturn(GAME_ID);
        given(request.getHost()).willReturn(HOST);
        given(request.getMembers()).willReturn(Arrays.asList(MEMBER_ID));
        given(gameItemLoader.loadItem(GAME_ID, GameItemType.GAME)).willReturn(Optional.of(gameModel));
        given(gameModel.getHost()).willReturn(HOST);

        underTest.loadGame(request);

        verify(gameLoader, timeout(1000)).loadGame(gameModel, Arrays.asList(MEMBER_ID));
    }
}