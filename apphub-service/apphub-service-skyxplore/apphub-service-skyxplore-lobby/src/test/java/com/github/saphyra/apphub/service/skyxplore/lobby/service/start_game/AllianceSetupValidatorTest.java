package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AllianceSetupValidatorTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    private final AllianceSetupValidator underTest = new AllianceSetupValidator();

    @Mock
    private AiPlayer aiPlayer;

    @Test
    void onlyOnePlayer() {
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .players(CollectionUtils.singleValueMap(USER_ID_1, null))
            .ais(Collections.emptyList())
            .build();

        underTest.check(request);
    }

    @Test
    void noAlliances() {
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .players(CollectionUtils.singleValueMap(USER_ID_1, null))
            .ais(List.of(aiPlayer))
            .build();

        given(aiPlayer.getAllianceId()).willReturn(null);

        underTest.check(request);
    }

    @Test
    void aiAndPlayerInTheSameAlliance() {
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .players(CollectionUtils.singleValueMap(USER_ID_1, ALLIANCE_ID))
            .ais(List.of(aiPlayer))
            .build();

        given(aiPlayer.getAllianceId()).willReturn(ALLIANCE_ID);

        Throwable ex = catchThrowable(() -> underTest.check(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.PRECONDITION_FAILED, ErrorCode.NOT_ENOUGH_ALLIANCES);
    }

    @Test
    void twoPlayersVsOneAi() {
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .players(Map.of(
                USER_ID_1, ALLIANCE_ID,
                USER_ID_2, ALLIANCE_ID
            ))
            .ais(List.of(aiPlayer))
            .build();

        given(aiPlayer.getAllianceId()).willReturn(null);

        underTest.check(request);
    }
}