package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MessageSenderThreadTest {
    @Mock
    private MessageSender messageSender;

    @Mock
    private ErrorReporterService errorReporterService;

    private MessageSenderThread underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageSenderThread(List.of(messageSender), errorReporterService);
    }

    @Mock
    private ExecutionResult<Boolean> messageSentExecutionResult;

    @Mock
    private ExecutionResult<Boolean> noMessageSentExecutionResult;

    @Mock
    private ExecutionResult<Boolean> errorExecutionResult;

    @Test
    void sendMessage() {
        given(messageSender.sendMessages()).willReturn(List.of(
            ConcurrentUtils.constantFuture(messageSentExecutionResult),
            ConcurrentUtils.constantFuture(noMessageSentExecutionResult),
            ConcurrentUtils.constantFuture(errorExecutionResult)
        ));

        given(messageSentExecutionResult.getOrThrow()).willReturn(true);
        given(noMessageSentExecutionResult.getOrThrow()).willReturn(false);
        RuntimeException exception = new RuntimeException();
        given(errorExecutionResult.getOrThrow()).willThrow(exception);

        long result = underTest.sendMessages();

        assertThat(result).isEqualTo(1);
        then(errorReporterService).should().report(anyString(), eq(exception));
    }
}