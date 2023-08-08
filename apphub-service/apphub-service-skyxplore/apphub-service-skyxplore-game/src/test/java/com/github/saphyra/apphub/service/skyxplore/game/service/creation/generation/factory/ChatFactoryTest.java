package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ChatFactoryTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID_3 = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChatFactory underTest;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Player player3;

    @Mock
    private Player player4;

    @Test
    public void createFromPlayerList() {
        given(idGenerator.randomUuid()).willAnswer(invocationOnMock -> UUID.randomUUID());

        given(player4.isAi()).willReturn(true);
        given(player1.getUserId()).willReturn(USER_ID_1);
        given(player2.getUserId()).willReturn(USER_ID_2);
        given(player3.getUserId()).willReturn(USER_ID_3);
        given(player1.getAllianceId()).willReturn(ALLIANCE_ID);
        given(player2.getAllianceId()).willReturn(ALLIANCE_ID);

        Chat result = underTest.create(Arrays.asList(player1, player2, player3, player4));

        assertThat(result.getPlayers()).containsExactlyInAnyOrder(USER_ID_1, USER_ID_2, USER_ID_3);
        assertThat(result.getRooms()).hasSize(3);
        assertRoom(result, GameConstants.CHAT_ROOM_GENERAL, USER_ID_1, USER_ID_2, USER_ID_3);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, USER_ID_1, USER_ID_2);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, USER_ID_3);
    }

    @Test
    public void create() {
        Map<UUID, UUID> players = CollectionUtils.toMap(
            new BiWrapper<>(USER_ID_1, ALLIANCE_ID),
            new BiWrapper<>(USER_ID_2, ALLIANCE_ID),
            new BiWrapper<>(USER_ID_3, null)
        );

        given(idGenerator.randomUuid()).willAnswer(invocationOnMock -> UUID.randomUUID());

        Chat result = underTest.create(players);

        assertThat(result.getPlayers()).containsExactlyInAnyOrder(USER_ID_1, USER_ID_2, USER_ID_3);
        assertThat(result.getRooms()).hasSize(3);
        assertRoom(result, GameConstants.CHAT_ROOM_GENERAL, USER_ID_1, USER_ID_2, USER_ID_3);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, USER_ID_1, USER_ID_2);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, USER_ID_3);
    }

    private void assertRoom(Chat result, String roomName, UUID... players) {
        boolean validationResult = result.getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getId().equals(roomName))
            .filter(chatRoom -> chatRoom.getMembers().size() == players.length)
            .anyMatch(chatRoom -> chatRoom.getMembers().containsAll(Arrays.asList(players)));

        assertThat(validationResult).isTrue();
    }
}