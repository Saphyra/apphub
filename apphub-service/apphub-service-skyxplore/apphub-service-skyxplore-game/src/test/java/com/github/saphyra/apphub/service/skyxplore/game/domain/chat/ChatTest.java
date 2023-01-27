package com.github.saphyra.apphub.service.skyxplore.game.domain.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ChatTest {
    @Mock
    private ChatRoom chatRoom;

    @Test
    public void addRoom() {
        Chat underTest = Chat.builder()
            .rooms(new ArrayList<>())
            .build();

        underTest.addRoom(chatRoom);

        assertThat(underTest.getRooms()).containsExactly(chatRoom);
    }
}