package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;

@ExtendWith(MockitoExtension.class)
public class ExecutorServiceBeanTest {
    private static final String TEST_RESULT = "asd";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Mock
    private ErrorReporterService errorReporterService;

    private ExecutorServiceBean underTest;

    @Mock
    private Helper helper;

    @BeforeEach
    void setUp() {
        underTest = ExecutorServiceBean.builder()
            .errorReporterService(errorReporterService)
            .executor(executorService)
            .build();
    }

    @Test
    void execute() {
        FutureWrapper<Void> result = underTest.execute(() -> helper.method());

        then(helper).should(timeout(1000)).method();
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().isSuccess()).isTrue();
    }

    @Test
    void execute_reportError() {
        RuntimeException exception = new RuntimeException("exception");
        doThrow(exception).when(helper).method();

        FutureWrapper<Void> result = underTest.execute(() -> helper.method());

        then(helper).should(timeout(1000)).method();
        result.get(1000, TimeUnit.MILLISECONDS);
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().isSuccess()).isFalse();
        then(errorReporterService).should().report(anyString(), eq(exception));
    }

    @Test
    void execute_doNotReportErrorWhenNotReported() {
        LoggedException exception = ExceptionFactory.loggedException("asd");
        doThrow(exception).when(helper).method();

        FutureWrapper<Void> result = underTest.execute(() -> helper.method());

        then(helper).should(timeout(1000)).method();
        result.get(1000, TimeUnit.MILLISECONDS);
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().isSuccess()).isFalse();
        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void forEach() {
        underTest.forEach(List.of(helper), Helper::method);

        then(helper).should().method();
    }

    @Test
    void processCollectionWithWait() {
        List<String> result = underTest.processCollectionWithWait(List.of(TEST_RESULT, TEST_RESULT), String::toUpperCase, 1);

        assertThat(result).hasSize(2)
            .containsOnly(TEST_RESULT.toUpperCase());
    }

    @Test
    void processCollectionWithWait_reportError() {
        RuntimeException cause = new RuntimeException("exception-message");
        Throwable ex = catchThrowable(() -> underTest.processCollectionWithWait(List.of(TEST_RESULT), s -> {
            throw cause;
        }, 1));

        assertThat(ex).isEqualTo(cause);
        then(errorReporterService).should().report(anyString(), eq(cause));
    }

    static class Helper {
        @SuppressWarnings("UnusedReturnValue")
        public String method() {
            return TEST_RESULT;
        }
    }
}