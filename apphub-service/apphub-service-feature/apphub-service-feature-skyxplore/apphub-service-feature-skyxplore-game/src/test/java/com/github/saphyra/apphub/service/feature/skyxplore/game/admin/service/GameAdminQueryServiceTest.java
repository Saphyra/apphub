package com.github.saphyra.apphub.service.feature.skyxplore.game.admin.service;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.feature.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameAdminQueryServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GameAdminQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameAdminQueryService.GameView gameView;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.GAME);
    }

    @Test
    void getAll() {
        given(gameDao.getAll()).willReturn(List.of(game));
        given(game.getGameId()).willReturn(GAME_ID);
        given(objectMapper.convertValue(game, GameAdminQueryService.GameView.class)).willReturn(gameView);

        CustomAssertions.singleListAssertThat(underTest.getAll(null))
            .returns(GAME_ID, SkyXploreGameDataEntry::getId)
            .returns(gameView, SkyXploreGameDataEntry::getData);
    }

    @Test
    void findById_notFound() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findById(GAME_ID, GAME_ID));
    }

    @Test
    void findById() {
        given(gameDao.findById(GAME_ID)).willReturn(Optional.of(game));
        given(objectMapper.convertValue(game, GameAdminQueryService.GameView.class)).willReturn(gameView);

        assertThat(underTest.findById(GAME_ID, GAME_ID))
            .returns(GAME_ID, SkyXploreGameDataDetails::getId)
            .returns(gameView, SkyXploreGameDataDetails::getData)
            .returns(List.of(), SkyXploreGameDataDetails::getReferencedBy)
            .extracting(skyXploreGameDataDetails -> skyXploreGameDataDetails.getRefersTo().size())
            .isEqualTo(GameItemType.values().length - 1);
    }
}