package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ReserveLevelTest {
    @Test
    void parse_null() {
        assertThat(com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel.parse("DepletedResources")).isEqualTo(com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel.DEPLETED);
    }
}