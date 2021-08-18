package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.StackTraceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionMapperTest {
    private static final String MESSAGE_1 = "message-1";
    private static final String MESSAGE_2 = "message-2";

    @Mock
    private StackTraceMapper stackTraceMapper;

    @InjectMocks
    private ExceptionMapper underTest;

    @Mock
    private Throwable throwable1;

    @Mock
    private Throwable throwable2;

    @Mock
    private StackTraceElement stackTraceElement1;

    @Mock
    private StackTraceElement stackTraceElement2;

    @Mock
    private StackTraceModel stackTraceModel1;

    @Mock
    private StackTraceModel stackTraceModel2;

    @Test
    public void map() {
        StackTraceElement[] stackTraceElements1 = {stackTraceElement1};
        given(throwable1.getStackTrace()).willReturn(stackTraceElements1);
        StackTraceElement[] stackTraceElements2 = {stackTraceElement2};
        given(throwable2.getStackTrace()).willReturn(stackTraceElements2);
        given(stackTraceMapper.mapStackTrace(stackTraceElements1)).willReturn(Arrays.asList(stackTraceModel1));
        given(stackTraceMapper.mapStackTrace(stackTraceElements2)).willReturn(Arrays.asList(stackTraceModel2));
        given(throwable1.getCause()).willReturn(throwable2);

        given(throwable1.getMessage()).willReturn(MESSAGE_1);
        given(throwable2.getMessage()).willReturn(MESSAGE_2);


        ExceptionModel result = underTest.map(throwable1);

        assertThat(result.getMessage()).isEqualTo(MESSAGE_1);
        assertThat(result.getType()).isEqualTo(throwable1.getClass().getName());
        assertThat(result.getThread()).isEqualTo(Thread.currentThread().getName());
        assertThat(result.getStackTrace()).containsExactly(stackTraceModel1);
        assertThat(result.getCause()).isNotNull();
        assertThat(result.getCause().getMessage()).isEqualTo(MESSAGE_2);
        assertThat(result.getCause().getType()).isEqualTo(throwable2.getClass().getName());
        assertThat(result.getCause().getThread()).isEqualTo(Thread.currentThread().getName());
        assertThat(result.getCause().getStackTrace()).containsExactly(stackTraceModel2);
        assertThat(result.getCause().getCause()).isNull();
    }
}