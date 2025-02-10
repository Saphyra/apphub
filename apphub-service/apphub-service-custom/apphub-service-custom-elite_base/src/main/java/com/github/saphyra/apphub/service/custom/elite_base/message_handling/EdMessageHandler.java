package com.github.saphyra.apphub.service.custom.elite_base.message_handling;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;

@RequiredArgsConstructor
@Component
@Slf4j
@ConditionalOnProperty(value = "elite-base.messageHandler.enabled", havingValue = "true", matchIfMissing = true)
public class EdMessageHandler implements MessageHandler {
    private final MessageFactory messageFactory;
    private final MessageDao messageDao;
    private final ErrorReporterService errorReporterService;
    private final PerformanceReporter performanceReporter;

    @Override
    @ServiceActivator(inputChannel = "zeroMqChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        byte[] output = new byte[256 * 1024];
        byte[] payload = (byte[]) message.getPayload();
        Inflater inflater = new Inflater();
        inflater.setInput(payload);
        try {
            int outputLength = inflater.inflate(output);
            String outputString = new String(output, 0, outputLength, StandardCharsets.UTF_8);
            log.debug("{}", outputString);
            EdMessage edMessage = messageFactory.create(outputString);
            performanceReporter.wrap(() -> messageDao.save(edMessage), PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportingKey.SAVE_NEW_MESSAGE.name());
        } catch (Exception e) {
            errorReporterService.report("Failed processing message", e);
            log.error("Failed processing message", e);
        }
    }
}
