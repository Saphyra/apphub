package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.AllianceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.PlayerConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.now();
    private static final Integer UNIVERSE_SIZE = 2345;

    @Mock
    private AllianceConverter allianceConverter;

    @Mock
    private PlayerConverter playerConverter;

    @Mock
    private GameDataConverter gameDataConverter;

    @InjectMocks
    private GameConverter underTest;

    @Mock
    private Player player;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private Alliance alliance;

    @Mock
    private AllianceModel allianceModel;

    @Mock
    private GameData gameData;

    @Mock
    private GameItem gameDataModel;

    @Test
    public void convertDeep() {
        Game game = Game.builder()
            .gameId(GAME_ID)
            .host(HOST)
            .gameName(GAME_NAME)
            .players(CollectionUtils.singleValueMap(UUID.randomUUID(), player))
            .alliances(CollectionUtils.singleValueMap(UUID.randomUUID(), alliance))
            .data(gameData)
            .lastPlayed(LAST_PLAYED)
            .markedForDeletion(true)
            .markedForDeletionAt(MARKED_FOR_DELETION_AT)
            .build();

        given(allianceConverter.toModel(alliance, game)).willReturn(allianceModel);
        given(playerConverter.toModel(player, game)).willReturn(playerModel);
        given(gameDataConverter.convert(GAME_ID, gameData)).willReturn(List.of(gameDataModel));
        given(gameData.getUniverseSize()).willReturn(UNIVERSE_SIZE);

        List<GameItem> result = underTest.convertDeep(game);

        GameModel expected = new GameModel();
        expected.setId(GAME_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.GAME);
        expected.setHost(HOST);
        expected.setName(GAME_NAME);
        expected.setLastPlayed(LAST_PLAYED);
        expected.setMarkedForDeletion(true);
        expected.setMarkedForDeletionAt(MARKED_FOR_DELETION_AT);
        expected.setUniverseSize(UNIVERSE_SIZE);

        assertThat(result).containsExactlyInAnyOrder(expected, playerModel, allianceModel, gameDataModel);
    }
}