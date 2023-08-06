package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WsMessageKeyTest {
    private static final UUID RECIPIENT = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID PAGE_ID = UUID.randomUUID();

    private final WsMessageKey underTest = WsMessageKey.builder()
        .recipient(RECIPIENT)
        .objectId(OBJECT_ID)
        .eventName(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED)
        .pages(List.of(OpenedPageType.MAP))
        .pageId(PAGE_ID)
        .build();

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private OpenedPage openedPage;

    @Test
    void pageNotOpened() {
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(RECIPIENT, player));
        given(player.getOpenedPage()).willReturn(openedPage);
        given(openedPage.getPageType()).willReturn(OpenedPageType.PLANET);

        assertThat(underTest.shouldBeSent(game)).isFalse();
    }

    @Test
    void differentPageId() {
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(RECIPIENT, player));
        given(player.getOpenedPage()).willReturn(openedPage);
        given(openedPage.getPageType()).willReturn(OpenedPageType.MAP);
        given(openedPage.getPageId()).willReturn(UUID.randomUUID());

        assertThat(underTest.shouldBeSent(game)).isFalse();
    }

    @Test
    void shouldBeSent() {
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(RECIPIENT, player));
        given(player.getOpenedPage()).willReturn(openedPage);
        given(openedPage.getPageType()).willReturn(OpenedPageType.MAP);
        given(openedPage.getPageId()).willReturn(PAGE_ID);

        assertThat(underTest.shouldBeSent(game)).isTrue();
    }
}