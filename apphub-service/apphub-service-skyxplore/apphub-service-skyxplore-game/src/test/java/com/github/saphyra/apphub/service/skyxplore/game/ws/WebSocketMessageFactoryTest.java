package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketMessageFactoryTest {
    private static final Object PAYLOAD = "asd";
    private static final UUID RECIPIENT = UUID.randomUUID();

    @InjectMocks
    private WebSocketMessageFactory underTest;

    @Test
    public void create() {
        WebSocketMessage result = underTest.create(List.of(RECIPIENT), WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED, PAYLOAD);

        assertThat(result.getRecipients()).containsExactlyInAnyOrder(RECIPIENT);
        assertThat(result.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED);
        assertThat(result.getEvent().getPayload()).isEqualTo(PAYLOAD);
    }
}