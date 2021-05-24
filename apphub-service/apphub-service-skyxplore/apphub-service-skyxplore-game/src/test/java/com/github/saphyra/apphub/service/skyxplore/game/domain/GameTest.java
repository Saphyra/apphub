package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Test
    public void filterConnectedPlayersFrom() {
        Game underTest = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, player1),
                new BiWrapper<>(USER_ID_2, player2)
            ))
            .build();

        given(player2.isConnected()).willReturn(true);

        List<UUID> result = underTest.filterConnectedPlayersFrom(Arrays.asList(USER_ID_1, USER_ID_2));

        assertThat(result).containsExactly(USER_ID_2);
    }
}