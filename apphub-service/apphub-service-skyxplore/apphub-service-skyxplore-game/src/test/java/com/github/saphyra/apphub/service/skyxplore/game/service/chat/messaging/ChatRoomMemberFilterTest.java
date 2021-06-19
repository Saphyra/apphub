package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChatRoomMemberFilterTest {
    private static final UUID SENDER = UUID.randomUUID();
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_2 = UUID.randomUUID();
    private static final UUID PLAYER_ID_3 = UUID.randomUUID();
    private static final String CUSTOM_CHAT_ROOM = "custom-chat-room";

    @InjectMocks
    private ChatRoomMemberFilter underTest;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Player player3;

    @Test
    public void generalRoom() {
        given(player1.isConnected()).willReturn(true);
        given(player2.isConnected()).willReturn(false);
        given(player1.getUserId()).willReturn(PLAYER_ID_1);

        List<UUID> result = underTest.getMembers(SENDER, GameConstants.CHAT_ROOM_GENERAL, Arrays.asList(chatRoom), CollectionUtils.toMap(new BiWrapper<>(PLAYER_ID_1, player1), new BiWrapper<>(PLAYER_ID_2, player2)));

        assertThat(result).containsExactly(PLAYER_ID_1);
    }

    @Test
    public void allianceRoom_found() {
        given(player1.isConnected()).willReturn(true);
        given(player2.isConnected()).willReturn(false);
        given(player1.getUserId()).willReturn(PLAYER_ID_1);

        given(chatRoom.getId()).willReturn(GameConstants.CHAT_ROOM_ALLIANCE);
        given(chatRoom.getMembers()).willReturn(Arrays.asList(PLAYER_ID_1, PLAYER_ID_2, SENDER));

        Map<UUID, Player> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, player1),
            new BiWrapper<>(PLAYER_ID_2, player2),
            new BiWrapper<>(SENDER, player3)
        );
        List<UUID> result = underTest.getMembers(SENDER, GameConstants.CHAT_ROOM_ALLIANCE, Arrays.asList(chatRoom), players);

        assertThat(result).containsExactly(PLAYER_ID_1);
    }

    @Test
    public void allianceRoom_notFound() {
        given(chatRoom.getId()).willReturn(CUSTOM_CHAT_ROOM);

        Map<UUID, Player> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, player1),
            new BiWrapper<>(PLAYER_ID_2, player2),
            new BiWrapper<>(PLAYER_ID_3, player3)
        );
        List<UUID> result = underTest.getMembers(SENDER, GameConstants.CHAT_ROOM_ALLIANCE, Arrays.asList(chatRoom), players);

        assertThat(result).containsExactly(SENDER);
    }

    @Test
    public void customRoom_found() {
        given(player1.isConnected()).willReturn(true);
        given(player2.isConnected()).willReturn(false);
        given(player1.getUserId()).willReturn(PLAYER_ID_1);

        given(chatRoom.getId()).willReturn(CUSTOM_CHAT_ROOM);
        given(chatRoom.getMembers()).willReturn(Arrays.asList(PLAYER_ID_1, PLAYER_ID_2));

        Map<UUID, Player> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, player1),
            new BiWrapper<>(PLAYER_ID_2, player2),
            new BiWrapper<>(PLAYER_ID_3, player3)
        );
        List<UUID> result = underTest.getMembers(SENDER, CUSTOM_CHAT_ROOM, Arrays.asList(chatRoom), players);

        assertThat(result).containsExactly(PLAYER_ID_1);
    }

    @Test
    public void customRoom_notFound() {
        given(chatRoom.getId()).willReturn("asd");

        Map<UUID, Player> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, player1),
            new BiWrapper<>(PLAYER_ID_2, player2),
            new BiWrapper<>(PLAYER_ID_3, player3)
        );
        List<UUID> result = underTest.getMembers(SENDER, CUSTOM_CHAT_ROOM, Arrays.asList(chatRoom), players);

        assertThat(result).containsExactly(SENDER);
    }
}