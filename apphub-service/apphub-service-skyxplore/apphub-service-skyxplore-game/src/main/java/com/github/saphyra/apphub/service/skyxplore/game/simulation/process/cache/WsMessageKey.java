package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
class WsMessageKey {
    @NonNull
    private final UUID recipient;

    @NonNull
    private final UUID objectId;

    @NonNull
    private final WebSocketEventName eventName;

    @NonNull
    private final List<OpenedPageType> pages;

    @Nullable
    private final UUID pageId;

    boolean shouldBeSent(Game game) {
        OpenedPage openedPage = game.getPlayers()
            .get(recipient)
            .getOpenedPage();
        return pages.contains(openedPage.getPageType()) && Objects.equals(pageId, openedPage.getPageId());
    }
}
