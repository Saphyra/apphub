package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.FixedExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.elite_base.config.EliteBaseProperties;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EdMessageProcessor {
    private final EliteBaseProperties properties;
    private final MessageDao messageDao;
    private final FixedExecutorServiceBean executorServiceBean;
    private final IdGenerator idGenerator;
    private final ErrorReporterService errorReporterService;
    private final List<MessageProcessor> messageProcessors;

    @SneakyThrows
    public synchronized void processMessages() {
        StopWatch stopWatch = StopWatch.createStarted();
        List<EdMessage> messages = messageDao.getMessages(properties.getMessageProcessorBatchSize());
        log.info("Processing {} messages.", messages.size());

        List<List<EdMessage>> partitions = ListUtils.partition(messages, properties.getMessageProcessorSublistSize());

        List<Future<ExecutionResult<Void>>> futures = partitions.stream()
            .map(edMessages -> executorServiceBean.execute(() -> processMessages(edMessages)))
            .toList();

        for (Future<ExecutionResult<Void>> future : futures) {
            future.get();
        }

        stopWatch.stop();
        log.info("{} messages processed in {}ms", messages.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private void processMessages(List<EdMessage> edMessages) {
        StopWatch stopWatch = StopWatch.createStarted();
        edMessages.forEach(this::processMessage);
        stopWatch.stop();
        log.debug("Processed {} messages in {}ms.", edMessages.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private void processMessage(EdMessage edMessage) {
        try {
            messageProcessors.stream()
                .filter(messageProcessor -> messageProcessor.canProcess(edMessage))
                .findAny()
                .ifPresentOrElse(
                    messageProcessor -> {
                        messageProcessor.processMessage(edMessage);
                        edMessage.setStatus(MessageStatus.PROCESSED);
                        messageDao.save(edMessage);
                    },
                    () -> {
                        log.info("Unhandled message: {}", edMessage);
                        edMessage.setStatus(MessageStatus.UNHANDLED);
                        messageDao.save(edMessage);
                    }
                );
        } catch (Exception e) {
            UUID exceptionId = idGenerator.randomUuid();
            log.error("Failed processing message. ExceptionId: {}", exceptionId, e);
            errorReporterService.report("Failed processing message. ExceptionId: " + exceptionId, e);
            edMessage.setStatus(MessageStatus.ERROR);
            edMessage.setExceptionId(exceptionId);
            messageDao.save(edMessage);
        }
    }
}
