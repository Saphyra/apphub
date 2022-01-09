package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreGamePageOpenedWebSocketHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Object PAYLOAD = "payload";

    @Mock
    private GameDao gameDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private SkyXploreGamePageOpenedWebSocketHandler underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private OpenedPage openedPage;

    @Test
    public void invalidValue() {
        given(objectMapperWrapper.convertValue(PAYLOAD, OpenedPage.class)).willReturn(openedPage);
        given(openedPage.getOpenedPageType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.handle(USER_ID, WebSocketEvent.builder().payload(PAYLOAD).build()));

        ExceptionValidator.validateInvalidParam(ex, "pageType", "must not be null");
    }

    @Test
    public void updateOpenedPage() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(objectMapperWrapper.convertValue(PAYLOAD, OpenedPage.class)).willReturn(openedPage);
        given(openedPage.getOpenedPageType()).willReturn(OpenedPageType.PLANET);

        underTest.handle(USER_ID, WebSocketEvent.builder().payload(PAYLOAD).build());

        verify(player).setOpenedPage(openedPage);
    }
}