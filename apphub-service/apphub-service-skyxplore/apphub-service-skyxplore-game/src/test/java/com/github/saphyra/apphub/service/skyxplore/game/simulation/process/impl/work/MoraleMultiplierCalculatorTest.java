package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.MoraleMultiplierCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MoraleMultiplierCalculatorTest {
    private static final Integer EFFICIENCY_DROP_UNDER = 2000;

    @Mock
    private GameProperties properties;

    @InjectMocks
    private MoraleMultiplierCalculator underTest;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Test
    void noEfficiencyDrop() {
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getWorkEfficiencyDropUnder()).willReturn(EFFICIENCY_DROP_UNDER);

        assertThat(underTest.calculateMoraleMultiplier(EFFICIENCY_DROP_UNDER + 1)).isEqualTo(1d);
    }

    @Test
    void decreasedEfficiency() {
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getWorkEfficiencyDropUnder()).willReturn(EFFICIENCY_DROP_UNDER);
        given(moraleProperties.getMinEfficiency()).willReturn(0.4);

        assertThat(underTest.calculateMoraleMultiplier(EFFICIENCY_DROP_UNDER / 2)).isEqualTo(0.5);
    }

    @Test
    void minimumEfficiency(){
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getWorkEfficiencyDropUnder()).willReturn(EFFICIENCY_DROP_UNDER);
        given(moraleProperties.getMinEfficiency()).willReturn(0.4);

        assertThat(underTest.calculateMoraleMultiplier(EFFICIENCY_DROP_UNDER / 3)).isEqualTo(0.4);
    }
}