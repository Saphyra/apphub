package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class Base64EncoderTest {
    private static final String TEXT = "text";

    @InjectMocks
    private Base64Encoder underTest;

    @Test
    public void encodeDecode() {
        String encoded = underTest.encode(TEXT);
        assertThat(encoded).isNotEqualTo(TEXT);
        String decoded = underTest.decode(encoded);
        assertThat(decoded).isEqualTo(TEXT);
    }
}