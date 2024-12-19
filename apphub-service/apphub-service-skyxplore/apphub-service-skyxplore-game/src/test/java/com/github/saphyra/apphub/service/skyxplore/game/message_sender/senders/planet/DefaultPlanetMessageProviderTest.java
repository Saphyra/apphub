package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultPlanetMessageProviderTest {
    private static final String ITEM_KEY = "item-key";
    private static final String RESPONSE = "response";
    private static final long POLLING_INTERVAL = 4352;
    private static final String SESSION_ID = "session-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String FIELD_LAST_MESSAGES = "lastMessages";

    @Mock
    private MessageSenderUtil messageSenderUtil;

    @Mock
    private DateTimeUtil dateTimeUtil;

    private DefaultPlanetMessageProvider<String> underTest;

    @Mock
    private WsSessionPlanetIdMapping mapping;

    @BeforeEach
    void setUp() {
        underTest = DefaultPlanetMessageProvider.<String>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(ITEM_KEY)
            .responseProvider((uuid, uuid2) -> RESPONSE)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(POLLING_INTERVAL)
            .build();
    }

    @Test
    void clearDisconnectedUserData() {
        underTest.clearDisconnectedUserData(List.of(mapping));

        then(messageSenderUtil).should().clearDisconnectedUserData(List.of(mapping), Collections.emptyMap());
    }

    @Test
    void getMessage_lastMessageValid() {
        given(messageSenderUtil.lastMessageValid(SESSION_ID, Collections.emptyMap(), POLLING_INTERVAL)).willReturn(true);

        assertThat(underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID)).isEmpty();
    }

    @Test
    void getMessage_responseNotChanged() throws IllegalAccessException {
        Map<String, LastMessage<Object>> lastMessages = Map.of(SESSION_ID, new LastMessage<>(RESPONSE, null));
        FieldUtils.writeDeclaredField(underTest, FIELD_LAST_MESSAGES, lastMessages, true);

        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, POLLING_INTERVAL)).willReturn(false);

        assertThat(underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID)).isEmpty();
    }

    @Test
    void getMessage() throws IllegalAccessException {
        Map<String, LastMessage<Object>> lastMessages = CollectionUtils.singleValueMap(SESSION_ID, new LastMessage<>("asd", null));
        FieldUtils.writeDeclaredField(underTest, FIELD_LAST_MESSAGES, lastMessages, true);

        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, POLLING_INTERVAL)).willReturn(false);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        Optional<UpdateItem> result = underTest.getMessage(SESSION_ID, USER_ID, PLANET_ID);

        assertThat(result).contains(new UpdateItem(ITEM_KEY, RESPONSE));

        //noinspection unchecked
        assertThat((Map<String, LastMessage<Object>>) FieldUtils.readField(underTest, FIELD_LAST_MESSAGES, true)).containsEntry(SESSION_ID, new LastMessage<>(RESPONSE, CURRENT_TIME));
    }
}