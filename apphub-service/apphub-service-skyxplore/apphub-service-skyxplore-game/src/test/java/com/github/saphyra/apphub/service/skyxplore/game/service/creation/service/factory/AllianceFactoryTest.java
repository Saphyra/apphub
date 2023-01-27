package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AllianceFactoryTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_2 = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";

    @InjectMocks
    private AllianceFactory underTest;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Test
    public void create() {
        Map<UUID, String> alliances = CollectionUtils.singleValueMap(ALLIANCE_ID, ALLIANCE_NAME);
        Map<UUID, UUID> members = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, ALLIANCE_ID),
            new BiWrapper<>(PLAYER_ID_2, null)
        );
        Map<UUID, Player> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, player1),
            new BiWrapper<>(PLAYER_ID_2, player2)
        );

        given(player1.getUserId()).willReturn(PLAYER_ID_1);

        Map<UUID, Alliance> result = underTest.create(alliances, members, players);

        assertThat(result).containsKeys(ALLIANCE_ID);
        assertThat(result.get(ALLIANCE_ID).getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.get(ALLIANCE_ID).getAllianceName()).isEqualTo(ALLIANCE_NAME);
        assertThat(result.get(ALLIANCE_ID).getMembers()).containsEntry(PLAYER_ID_1, player1);
        verify(player1).setAllianceId(ALLIANCE_ID);
    }
}