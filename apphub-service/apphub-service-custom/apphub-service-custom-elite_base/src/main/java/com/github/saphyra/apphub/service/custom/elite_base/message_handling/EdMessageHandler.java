package com.github.saphyra.apphub.service.custom.elite_base.message_handling;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.zip.Inflater;

@Component
@Slf4j
@ConditionalOnProperty(value = "elite-base.messageHandler.enabled", havingValue = "true", matchIfMissing = true)
public class EdMessageHandler implements MessageHandler {
    private final MessageFactory messageFactory;
    private final MessageDao messageDao;
    private final ErrorReporterService errorReporterService;
    private final PerformanceReporter performanceReporter;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties eliteBaseProperties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final ApplicationContextProxy applicationContextProxy;

    private volatile LocalDateTime lastMessage;

    public EdMessageHandler(
        MessageFactory messageFactory,
        MessageDao messageDao,
        ErrorReporterService errorReporterService,
        PerformanceReporter performanceReporter,
        DateTimeUtil dateTimeUtil,
        ScheduledExecutorServiceBean scheduledExecutorServiceBean,
        EliteBaseProperties eliteBaseProperties,
        ApplicationContextProxy applicationContextProxy
    ) {
        this.messageFactory = messageFactory;
        this.messageDao = messageDao;
        this.errorReporterService = errorReporterService;
        this.performanceReporter = performanceReporter;
        this.dateTimeUtil = dateTimeUtil;
        lastMessage = dateTimeUtil.getCurrentDateTime();
        this.eliteBaseProperties = eliteBaseProperties;
        this.scheduledExecutorServiceBean = scheduledExecutorServiceBean;
        this.applicationContextProxy = applicationContextProxy;
    }

    @Override
    @ServiceActivator(inputChannel = "zeroMqChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        byte[] output = new byte[256 * 1024];
        byte[] payload = (byte[]) message.getPayload();
        Inflater inflater = new Inflater();
        inflater.setInput(payload);
        try {
            lastMessage = dateTimeUtil.getCurrentDateTime();
            int outputLength = inflater.inflate(output);
            String outputString = new String(output, 0, outputLength, StandardCharsets.UTF_8);
            log.debug("{}", outputString);
            EdMessage edMessage = messageFactory.create(outputString);
            performanceReporter.wrap(() -> messageDao.save(edMessage), PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING, PerformanceReportingKey.SAVE_NEW_MESSAGE.name());
        } catch (MessageProcessingDelayedException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            errorReporterService.report("Failed processing message", e);
            log.error("Failed processing message", e);
        }
    }

    @PostConstruct
    void scheduleChecked() {
        scheduledExecutorServiceBean.scheduleFixedRate(this::shutdownIfTimeout, Duration.of(10, ChronoUnit.SECONDS));
    }

    private void shutdownIfTimeout() {
        log.info("Checking connection...");
        LocalDateTime expirationTime = dateTimeUtil.getCurrentDateTime()
            .minus(eliteBaseProperties.getIncomingMessageTimeout());

        log.info("Last message arrived at {}. Current time: {}", lastMessage, expirationTime);

        if (expirationTime.isAfter(lastMessage)) {
            log.error("Last message arrived at {}. Shutting down service...", lastMessage);
            errorReporterService.report(String.format("Last message came at %s. Shutting down service...", lastMessage));
            shutdown();
        }
    }

    private void shutdown() {
        SpringApplication.exit(applicationContextProxy.getContext(), () -> 0);

        System.exit(0);
    }
}
