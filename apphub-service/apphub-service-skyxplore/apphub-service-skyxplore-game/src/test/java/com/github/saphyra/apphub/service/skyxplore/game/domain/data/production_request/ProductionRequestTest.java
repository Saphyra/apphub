package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionRequestTest {
    @Test
    void increaseDispatchedAmount() {
        ProductionRequest underTest = ProductionRequest.builder()
            .dispatchedAmount(10)
            .build();

        underTest.increaseDispatchedAmount(2);

        assertThat(underTest.getDispatchedAmount()).isEqualTo(12);
    }
}