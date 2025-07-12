package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderThread {
    private final List<MessageSender> messageSenders;
    private final ErrorReporterService errorReporterService;

    @Scheduled(fixedDelayString = "${game.tickTimeMillis}")
    long sendMessages() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.debug("Updating Clients through WebSocket...");
        List<FutureWrapper<Boolean>> result = messageSenders.stream()
            .flatMap(messageSender -> messageSender.sendMessages().stream())
            .toList();

        long messagesSent = result.stream()
            .filter(future -> {
                try {
                    return future.get()
                        .getOrThrow();
                } catch (Exception e) {
                    errorReporterService.report("Unhandled exception during message sending", e);
                    return false;
                }
            })
            .count();

        stopWatch.stop();
        if (messagesSent > 0) {
            log.info("Clients were updated through WebSocket in {}ms. {} messages were sent.", stopWatch.getTime(TimeUnit.MILLISECONDS), result.size());
        }

        return messagesSent;
    }
}
