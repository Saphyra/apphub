package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameToGameItemListConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.now();

    @Mock
    private AllianceToModelConverter allianceConverter;

    @Mock
    private PlayerToModelConverter playerConverter;

    @Mock
    private UniverseToModelConverter universeConverter;

    @InjectMocks
    private GameToGameItemListConverter underTest;

    @Mock
    private Player player;

    @Mock
    private PlayerModel playerModel;

    @Mock
    private Alliance alliance;

    @Mock
    private AllianceModel allianceModel;

    @Mock
    private Universe universe;

    @Mock
    private UniverseModel universeModel;

    @Test
    public void convertDeep() {
        Game game = Game.builder()
            .gameId(GAME_ID)
            .host(HOST)
            .gameName(GAME_NAME)
            .players(CollectionUtils.singleValueMap(UUID.randomUUID(), player))
            .alliances(CollectionUtils.singleValueMap(UUID.randomUUID(), alliance))
            .universe(universe)
            .lastPlayed(LAST_PLAYED)
            .markedForDeletion(true)
            .markedForDeletionAt(MARKED_FOR_DELETION_AT)
            .build();

        given(universeConverter.convertDeep(universe, game)).willReturn(Arrays.asList(universeModel));
        given(allianceConverter.convert(alliance, game)).willReturn(allianceModel);
        given(playerConverter.convert(player, game)).willReturn(playerModel);

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

        assertThat(result).containsExactlyInAnyOrder(expected, playerModel, allianceModel, universeModel);
    }
}