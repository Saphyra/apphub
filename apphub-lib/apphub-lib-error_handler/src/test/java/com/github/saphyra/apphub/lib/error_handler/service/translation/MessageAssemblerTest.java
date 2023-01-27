package com.github.saphyra.apphub.lib.error_handler.service.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MessageAssemblerTest {
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String MESSAGE = "asd ${key} asd";
    private static final String UNKNOWN_KEY = "unknown-key";
    private static final String UNKNOWN_VALUE = "unknown-value";

    @InjectMocks
    private MessageAssembler underTest;

    @Test
    public void assembleMessage() {
        Map<String, String> params = new HashMap<>();
        params.put(KEY, VALUE);
        params.put(UNKNOWN_KEY, UNKNOWN_VALUE);

        String result = underTest.assembleMessage(MESSAGE, params);

        assertThat(result).isEqualTo("asd value asd");
    }
}