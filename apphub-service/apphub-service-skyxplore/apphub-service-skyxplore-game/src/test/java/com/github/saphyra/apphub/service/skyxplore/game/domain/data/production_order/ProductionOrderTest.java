package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionOrderTest {
    @Test
    void allStarted() {
        ProductionOrder underTest = ProductionOrder.builder()
            .startedAmount(20)
            .requestedAmount(20)
            .build();

        assertThat(underTest.allStarted()).isTrue();
    }

    @Test
    void allStarted_notStarted() {
        ProductionOrder underTest = ProductionOrder.builder()
            .startedAmount(19)
            .requestedAmount(20)
            .build();

        assertThat(underTest.allStarted()).isFalse();
    }

    @Test
    void getMissingAmount() {
        ProductionOrder underTest = ProductionOrder.builder()
            .startedAmount(19)
            .requestedAmount(20)
            .build();

        assertThat(underTest.getMissingAmount()).isEqualTo(1);
    }

    @Test
    void increaseStartedAmount() {
        ProductionOrder underTest = ProductionOrder.builder()
            .startedAmount(19)
            .build();

        underTest.increaseStartedAmount(10);

        assertThat(underTest.getStartedAmount()).isEqualTo(29);
    }
}