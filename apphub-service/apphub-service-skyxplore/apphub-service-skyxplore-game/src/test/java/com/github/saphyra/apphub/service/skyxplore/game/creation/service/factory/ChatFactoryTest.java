package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChatFactoryTest {
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_2 = UUID.randomUUID();
    private static final UUID PLAYER_ID_3 = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChatFactory underTest;

    @Test
    public void create() {
        Map<UUID, UUID> players = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, ALLIANCE_ID),
            new BiWrapper<>(PLAYER_ID_2, ALLIANCE_ID),
            new BiWrapper<>(PLAYER_ID_3, null)
        );

        given(idGenerator.randomUuid()).willAnswer(invocationOnMock -> UUID.randomUUID());

        Chat result = underTest.create(players);

        assertThat(result.getPlayers()).containsExactlyInAnyOrder(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3);
        assertThat(result.getRooms()).hasSize(3);
        assertRoom(result, GameConstants.CHAT_ROOM_GENERAL, PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, PLAYER_ID_1, PLAYER_ID_2);
        assertRoom(result, GameConstants.CHAT_ROOM_ALLIANCE, PLAYER_ID_3);
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