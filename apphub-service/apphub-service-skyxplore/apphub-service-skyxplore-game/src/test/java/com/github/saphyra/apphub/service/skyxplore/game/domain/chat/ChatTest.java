package com.github.saphyra.apphub.service.skyxplore.game.domain.chat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
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