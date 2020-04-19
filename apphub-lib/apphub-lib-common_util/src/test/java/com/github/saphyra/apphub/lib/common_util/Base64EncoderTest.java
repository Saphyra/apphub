package com.github.saphyra.apphub.lib.common_util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
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