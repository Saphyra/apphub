package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.StackTraceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StackTraceMapperTest {
    private static final String FILE_NAME = "file-name";
    private static final String CLASS_NAME = "class-name";
    private static final String METHOD_NAME = "method-name";
    private static final Integer LINE_NUMBER = 42;

    @InjectMocks
    private StackTraceMapper underTest;

    @Mock
    private StackTraceElement stackTraceElement;

    @Test
    public void mapStackTrace() {
        given(stackTraceElement.getFileName()).willReturn(FILE_NAME);
        given(stackTraceElement.getClassName()).willReturn(CLASS_NAME);
        given(stackTraceElement.getMethodName()).willReturn(METHOD_NAME);
        given(stackTraceElement.getLineNumber()).willReturn(LINE_NUMBER);

        List<StackTraceModel> result = underTest.mapStackTrace(new StackTraceElement[]{stackTraceElement});

        StackTraceModel expected = StackTraceModel.builder()
            .fileName(FILE_NAME)
            .className(CLASS_NAME)
            .methodName(METHOD_NAME)
            .lineNumber(LINE_NUMBER)
            .build();

        assertThat(result).containsExactly(expected);
    }
}