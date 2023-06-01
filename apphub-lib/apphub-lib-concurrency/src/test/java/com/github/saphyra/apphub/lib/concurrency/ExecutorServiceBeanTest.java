package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExecutorServiceBeanTest {
    public static final String TEST_RESULT = "asd";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Mock
    private ErrorReporterService errorReporterService;

    private ExecutorServiceBean underTest;

    @Mock
    private Helper helper;

    @BeforeEach
    public void setUp() {
        underTest = ExecutorServiceBean.builder()
            .errorReporterService(errorReporterService)
            .executor(executorService)
            .build();
    }

    @Test
    public void execute() throws ExecutionException, InterruptedException {
        Future<ExecutionResult<Void>> result = underTest.execute(() -> helper.method());

        verify(helper, timeout(1000)).method();
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().isSuccess()).isTrue();
    }

    @Test
    public void execute_reportError() throws ExecutionException, InterruptedException {
        RuntimeException exception = new RuntimeException("exception");
        doThrow(exception).when(helper).method();

        Future<ExecutionResult<Void>> result = underTest.execute(() -> helper.method());

        verify(helper, timeout(1000)).method();
        for (int i = 0; i < 10 && !result.isDone(); i++) {
            Thread.sleep(100);
        }
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().isSuccess()).isFalse();
        verify(errorReporterService).report(anyString(), eq(exception));
    }

    @Test
    public void forEach() {
        underTest.forEach(Arrays.asList(helper), Helper::method);

        verify(helper).method();
    }

    @Test
    public void processCollectionWithWait() {
        List<String> result = underTest.processCollectionWithWait(Arrays.asList(TEST_RESULT, TEST_RESULT), String::toUpperCase, 1);

        assertThat(result).hasSize(2)
            .containsOnly(TEST_RESULT.toUpperCase());
    }

    @Test
    public void processCollectionWithWait_reportError() {
        RuntimeException cause = new RuntimeException("exception-message");
        Throwable ex = catchThrowable(() -> underTest.processCollectionWithWait(Arrays.asList(TEST_RESULT), s -> {
            throw cause;
        }, 1));

        assertThat(ex).isEqualTo(cause);
        verify(errorReporterService).report(anyString(), eq(cause));
    }

    public static class Helper {
        @SuppressWarnings("UnusedReturnValue")
        public String method() {
            return TEST_RESULT;
        }
    }
}