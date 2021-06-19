package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChatRoomFactoryTest {
    private static final String CHAT_ROOM_ID = "chat-room-id";
    private static final UUID CREATOR = UUID.randomUUID();
    private static final UUID MEMBER = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChatRoomFactory underTest;

    @Test
    public void create() {
        given(idGenerator.generateRandomId()).willReturn(CHAT_ROOM_ID);

        ChatRoom result = underTest.create(CREATOR, Arrays.asList(MEMBER));

        assertThat(result.getId()).isEqualTo(CHAT_ROOM_ID);
        assertThat(result.getMembers()).containsExactlyInAnyOrder(CREATOR, MEMBER);
    }
}