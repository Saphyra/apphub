package com.github.saphyra.service.elite_base.message_handling;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.service.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.service.elite_base.message_handling.dao.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class EdMessageHandler implements MessageHandler {
    private final MessageFactory messageFactory;
    private final MessageDao messageDao;
    private final ErrorReporterService errorReporterService;

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
            log.info("{}", outputString);
            EdMessage edMessage = messageFactory.create(outputString);
            messageDao.save(edMessage);
        } catch (Exception e) {
            errorReporterService.report("Failed processing message", e);
            log.error("Failed processing message", e);
        }
    }
}
