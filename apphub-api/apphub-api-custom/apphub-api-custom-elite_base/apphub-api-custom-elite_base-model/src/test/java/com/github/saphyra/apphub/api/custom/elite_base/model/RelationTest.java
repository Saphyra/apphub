package com.github.saphyra.apphub.api.custom.elite_base.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RelationTest {
    private static final String VALUE_1 = "value-1";
    private static final String VALUE_2 = "value-2";
    private static final String VALUE_3 = "value-3";
    private static final String VALUE_4 = "value-4";

    @Test
    void anyMatch_match() {
        List<String> current = List.of(VALUE_1, VALUE_2);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.ANY_MATCH.apply(required, () -> current)).isTrue();
    }

    @Test
    void anyMatch_mismatch() {
        List<String> current = List.of(VALUE_1);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.ANY_MATCH.apply(required, () -> current)).isFalse();
    }

    @Test
    void allMatch_match() {
        List<String> current = List.of(VALUE_1, VALUE_2, VALUE_3);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.ALL_MATCH.apply(required, () -> current)).isTrue();
    }

    @Test
    void allMatch_mismatch() {
        List<String> current = List.of(VALUE_1, VALUE_3);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.ALL_MATCH.apply(required, () -> current)).isFalse();
    }

    @Test
    void noneMatch_match() {
        List<String> current = List.of(VALUE_1, VALUE_4);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.NONE_MATCH.apply(required, () -> current)).isTrue();
    }

    @Test
    void noneMatch_mismatch() {
        List<String> current = List.of(VALUE_1, VALUE_3);
        List<String> required = List.of(VALUE_2, VALUE_3);

        assertThat(Relation.NONE_MATCH.apply(required, () -> current)).isFalse();
    }

    @Test
    void empty_match() {
        List<String> current = List.of();

        assertThat(Relation.EMPTY.apply(Collections.emptyList(), () -> current)).isTrue();
    }

    @Test
    void empty_mismatch() {
        List<String> current = List.of(VALUE_2);

        assertThat(Relation.EMPTY.apply(Collections.emptyList(), () -> current)).isFalse();
    }

    @Test
    void any() {
        assertThat(Relation.ANY.apply(Collections.emptyList(), () -> List.of(VALUE_2))).isTrue();
    }
}