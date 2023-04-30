package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WsMessageKeyFactoryTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID PAGE_ID = UUID.randomUUID();

    private final WsMessageKeyFactory underTest = new WsMessageKeyFactory();

    @Test
    void create() {
        WsMessageKey result = underTest.create(RECIPIENT, WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, OBJECT_ID, List.of(OpenedPageType.NONE), PAGE_ID);

        assertThat(result.getRecipient()).isEqualTo(RECIPIENT);
        assertThat(result.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED);
        assertThat(result.getObjectId()).isEqualTo(OBJECT_ID);
        assertThat(result.getPages()).containsExactly(OpenedPageType.NONE);
        assertThat(result.getPageId()).isEqualTo(PAGE_ID);
    }
}