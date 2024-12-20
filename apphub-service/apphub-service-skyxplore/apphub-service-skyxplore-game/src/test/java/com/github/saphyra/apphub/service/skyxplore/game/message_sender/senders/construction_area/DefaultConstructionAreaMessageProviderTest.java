package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultConstructionAreaMessageProviderTest {
    private static final String ITEM_KEY = "item_key";
    private static final Long POLLING_INTERVAL = 34L;
    private static final String SESSION_ID = "session-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String MESSAGE = "message";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();


    @Mock
    private MessageSenderUtil messageSenderUtil;

    @Mock
    private BiFunction<UUID, UUID, String> responseProvider;

    @Mock
    private DateTimeUtil dateTimeUtil;

    private DefaultConstructionAreaMessageProvider<String> underTest;

    private final Map<String, LastMessage<String>> lastMessages = new HashMap<>();

    @Mock
    private WsSessionConstructionAreaIdMapping connectedUser;

    @BeforeEach
    void setUp() {
        underTest = DefaultConstructionAreaMessageProvider.<String>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(ITEM_KEY)
            .responseProvider(responseProvider)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(POLLING_INTERVAL)
            .lastMessages(lastMessages)
            .build();
    }

    @Test
    void clearDisconnectedUserData() {
        underTest.clearDisconnectedUserData(List.of(connectedUser));

        then(messageSenderUtil).should().clearDisconnectedUserData(List.of(connectedUser), lastMessages);
    }

    @Test
    void getMessage_lastMessageValid() {
        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, POLLING_INTERVAL)).willReturn(true);

        assertThat(underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID)).isEmpty();
    }

    @Test
    void getMessage_noChangeInPayload() {
        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, POLLING_INTERVAL)).willReturn(false);
        given(responseProvider.apply(USER_ID, PLANET_ID)).willReturn(MESSAGE);
        lastMessages.put(SESSION_ID, new LastMessage<>(MESSAGE, null));

        assertThat(underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID)).isEmpty();
    }

    @Test
    void getMessage() {
        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, POLLING_INTERVAL)).willReturn(false);
        given(responseProvider.apply(USER_ID, PLANET_ID)).willReturn(MESSAGE);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        assertThat(underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID)).contains(new UpdateItem(ITEM_KEY, MESSAGE));

        assertThat(lastMessages).containsEntry(SESSION_ID, new LastMessage<>(MESSAGE, CURRENT_TIME));
    }
}