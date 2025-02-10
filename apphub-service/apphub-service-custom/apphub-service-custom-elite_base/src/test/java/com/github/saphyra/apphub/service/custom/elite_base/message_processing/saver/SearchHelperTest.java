package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SearchHelperTest {
    private static final String VALUE = "value";

    @Test
    void skipSearch() {
        assertThat(new SearchHelper<>((String) null, () -> Optional.of(VALUE)).search()).isEmpty();
    }

    @Test
    void search() {
        assertThat(new SearchHelper<>(VALUE, () -> Optional.of(VALUE)).search()).contains(VALUE);
    }
}