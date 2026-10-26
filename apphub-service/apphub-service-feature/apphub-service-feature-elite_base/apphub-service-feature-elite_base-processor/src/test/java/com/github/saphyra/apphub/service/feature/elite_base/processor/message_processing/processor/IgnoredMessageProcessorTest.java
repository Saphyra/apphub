package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IgnoredMessageProcessorTest {
    @InjectMocks
    private IgnoredMessageProcessor underTest;

    @Mock
    private EdMessage message;

    public static Stream<Arguments> params() {
        return Stream.of(
            Arguments.of(SchemaRefs.SCAN_ORGANIC)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void canProcess(String schemaRef) {
        given(message.getSchemaRef()).willReturn(schemaRef);

        assertThat(underTest.canProcess(message)).isTrue();
    }
}