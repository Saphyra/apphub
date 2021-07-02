package com.github.saphyra.apphub.lib.common_util.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NullSafeConverterTest {

    @Test
    public void convertNull() {
        Integer result = NullSafeConverter.safeConvert(null, String::length);
        assertThat(result).isNull();
    }

    @Test
    public void convertValue() {
        Integer result = NullSafeConverter.safeConvert("asd", String::length);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void convertToDefault() {
        Integer result = NullSafeConverter.safeConvert(null, String::length, 3);
        assertThat(result).isEqualTo(3);
    }
}