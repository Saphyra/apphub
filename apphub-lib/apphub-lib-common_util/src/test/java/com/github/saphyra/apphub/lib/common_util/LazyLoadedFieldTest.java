package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LazyLoadedFieldTest {
    private static final String DATA = "data";

    @Mock
    private Supplier<String> supplier;

    @Test
    void get(){
        given(supplier.get()).willReturn(DATA);

        LazyLoadedField<String> underTest = new LazyLoadedField<>(supplier);

        assertThat(underTest.get()).isEqualTo(DATA);
        assertThat(underTest.get()).isEqualTo(DATA);

        then(supplier).should().get();
    }
}