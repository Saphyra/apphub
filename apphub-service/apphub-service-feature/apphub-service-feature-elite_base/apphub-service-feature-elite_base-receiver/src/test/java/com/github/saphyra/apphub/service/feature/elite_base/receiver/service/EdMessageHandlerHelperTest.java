package com.github.saphyra.apphub.service.feature.elite_base.receiver.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.receiver.config.EliteBaseReceiverProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_INCOMING;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_INCOMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EdMessageHandlerHelperTest {
    private static final LocalDateTime LAST_MESSAGE = LocalDateTime.now();
    private static final Duration INCOMING_MESSAGE_TIMEOUT = Duration.ofMinutes(2);
    private static final String PAYLOAD = "payload";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EliteBaseReceiverProperties eliteBaseReceiverProperties;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private MessageFactory messageFactory;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EdMessageHandlerHelper underTest;

    @Mock
    private ConfigurableApplicationContext applicationContext;

    @Mock
    private EdMessage message;

    @Test
    void handleMessage() {
        given(messageFactory.create(PAYLOAD)).willReturn(message);

        underTest.handleMessage(PAYLOAD);

        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_INCOMING, MESSAGE_ELITE_BASE_INCOMING, message);
    }

    @Test
    void shutdownDisabled() {
        given(eliteBaseReceiverProperties.isShutdownEnabled()).willReturn(false);

        assertThat(underTest.shutdownNeeded(LAST_MESSAGE)).isFalse();

        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void shutdownNeeded() {
        given(eliteBaseReceiverProperties.isShutdownEnabled()).willReturn(true);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(LAST_MESSAGE.plus(INCOMING_MESSAGE_TIMEOUT).plusSeconds(1));
        given(eliteBaseReceiverProperties.getIncomingMessageTimeout()).willReturn(INCOMING_MESSAGE_TIMEOUT);

        assertThat(underTest.shutdownNeeded(LAST_MESSAGE)).isTrue();

        then(errorReporterService).should().report(anyString());
    }

    @Test
    void shutdownIsNotNeeded() {
        given(eliteBaseReceiverProperties.isShutdownEnabled()).willReturn(true);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(LAST_MESSAGE.plus(INCOMING_MESSAGE_TIMEOUT).minusSeconds(1));
        given(eliteBaseReceiverProperties.getIncomingMessageTimeout()).willReturn(INCOMING_MESSAGE_TIMEOUT);

        assertThat(underTest.shutdownNeeded(LAST_MESSAGE)).isFalse();

        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void shutdown() {
        given(applicationContextProxy.getContext()).willReturn(applicationContext);

        underTest.shutdown();

        applicationContext.stop();

        then(applicationContext).should().registerShutdownHook();
        then(applicationContext).should().close();
    }
}