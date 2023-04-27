package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlanetsTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    private final Planets underTest = new Planets();

    @Mock
    private Planet planet1;

    @Mock
    private Planet planet2;

    @Test
    void getBySolarSystemId() {
        given(planet1.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(planet2.getSolarSystemId()).willReturn(UUID.randomUUID());

        underTest.put(UUID.randomUUID(), planet1);
        underTest.put(UUID.randomUUID(), planet2);

        assertThat(underTest.getBySolarSystemId(SOLAR_SYSTEM_ID)).containsExactly(planet1);
    }
}