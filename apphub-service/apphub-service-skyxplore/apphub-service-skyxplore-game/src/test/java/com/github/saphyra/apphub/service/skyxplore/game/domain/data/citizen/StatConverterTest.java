package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStat;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.StatResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StatConverterTest {
    private static final Integer MORALE_MAX = 234;
    private static final Integer SATIETY_MAX = 457;
    private static final Integer SATIETY = 321;
    private static final Integer MORALE = 67;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private StatConverter underTest;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenProperties citizenProperties;

    @Test
    void convert() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxStatValues()).willReturn(Map.of(
            CitizenStat.MORALE, MORALE_MAX,
            CitizenStat.SATIETY, SATIETY_MAX
        ));
        given(citizen.getSatiety()).willReturn(SATIETY);
        given(citizen.getMorale()).willReturn(MORALE);

        Map<CitizenStat, StatResponse> result = underTest.convert(citizen);

        assertThat(result)
            .returns(MORALE_MAX, citizenStatStatResponseMap -> citizenStatStatResponseMap.get(CitizenStat.MORALE).getMaxValue())
            .returns(MORALE, citizenStatStatResponseMap -> citizenStatStatResponseMap.get(CitizenStat.MORALE).getValue())
            .returns(SATIETY_MAX, citizenStatStatResponseMap -> citizenStatStatResponseMap.get(CitizenStat.SATIETY).getMaxValue())
            .returns(SATIETY, citizenStatStatResponseMap -> citizenStatStatResponseMap.get(CitizenStat.SATIETY).getValue());
    }
}