package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultCheckerTest {
    private static final Object VALUE = "value";

    @Test
    void nullNewValue() {
        assertThat(new DefaultChecker(null, () -> VALUE).check()).isTrue();
    }

    @Test
    void equalValues() {
        assertThat(new DefaultChecker(VALUE, () -> VALUE).check()).isTrue();
    }

    @Test
    void differentValues() {
        assertThat(new DefaultChecker("asd", () -> VALUE).check()).isFalse();
    }

    @Test
    void equalArrays() {
        assertThat(new DefaultChecker(new Object[0], () -> new Object[0]).check()).isTrue();
    }
}