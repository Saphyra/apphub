package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID_3 = UUID.randomUUID();

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Player player3;

    @Test
    public void filterConnectedPlayersFrom() {
        Game underTest = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, player1),
                new BiWrapper<>(USER_ID_2, player2),
                new BiWrapper<>(USER_ID_3, player3)
            ))
            .build();

        given(player2.isConnected()).willReturn(true);
        given(player3.isAi()).willReturn(true);

        List<UUID> result = underTest.filterConnectedPlayersFrom(Arrays.asList(USER_ID_1, USER_ID_2, USER_ID_3));

        assertThat(result).containsExactly(USER_ID_2);
    }

    @Test
    public void getConnectedPlayers() {
        Game underTest = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, player1),
                new BiWrapper<>(USER_ID_2, player2),
                new BiWrapper<>(USER_ID_3, player3)
            ))
            .build();

        given(player1.isConnected()).willReturn(true);
        given(player2.isAi()).willReturn(true);
        given(player1.getUserId()).willReturn(USER_ID_1);

        List<UUID> result = underTest.getConnectedPlayers();

        assertThat(result).containsExactly(USER_ID_1);
    }
}