package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MissingResourcesTest {
    @Test
    void decreaseToRequest() {
        MissingResources underTest = new MissingResources(10, 5);

        underTest.decreaseToRequest(3);

        assertThat(underTest.getToRequest()).isEqualTo(2);
    }
}