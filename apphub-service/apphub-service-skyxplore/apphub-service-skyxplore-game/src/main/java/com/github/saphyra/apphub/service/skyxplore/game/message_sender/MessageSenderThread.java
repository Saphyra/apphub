package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MessageSenderThread {
    private final List<MessageSender> messageSenders;
    private final ErrorReporterService errorReporterService;

    @Scheduled(fixedDelayString = "${game.tickTimeMillis}")
    private void sendMessages() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.debug("Updating Clients through WebSocket...");
        List<Future<ExecutionResult<Boolean>>> result = messageSenders.stream()
            .flatMap(messageSender -> messageSender.sendMessages().stream())
            .toList();

        long messagesSent = result.stream()
            .filter(future -> {
                try {
                    Boolean isMessageSent = future.get()
                        .getOrHandle(e -> errorReporterService.report("Unhandled exception during message sending", e))
                        .getEntity2();
                    return Optional.ofNullable(isMessageSent)
                        .orElse(false);
                } catch (InterruptedException | ExecutionException e) {
                    errorReporterService.report("Unhandled exception during message sending", e);
                    return false;
                }
            })
            .count();

        stopWatch.stop();
        if (messagesSent > 0) {
            log.info("Clients were updated through WebSocket in {}ms. {} messages were sent.", stopWatch.getTime(TimeUnit.MILLISECONDS), result.size());
        }
    }
}
