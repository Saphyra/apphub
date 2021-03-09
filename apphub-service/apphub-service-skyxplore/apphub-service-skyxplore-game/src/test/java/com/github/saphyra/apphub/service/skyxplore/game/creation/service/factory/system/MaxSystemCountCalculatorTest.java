package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;

@RunWith(MockitoJUnitRunner.class)
public class MaxSystemCountCalculatorTest {
    private static final int UNIVERSE_SIZE = 23423;
    private static final Double SIZE_MULTIPLIER = 2d;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private Random random;

    @InjectMocks
    private MaxSystemCountCalculator underTest;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Test
    public void randomAmount() {
        given(random.randInt(0, 2)).willReturn(1);
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getSizeMultiplier()).willReturn(CollectionUtils.singleValueMap(SystemAmount.MEDIUM, SIZE_MULTIPLIER));

        int result = underTest.getMaxAllocationTryCount(UNIVERSE_SIZE, SystemAmount.RANDOM);

        assertThat(result).isEqualTo((int) (UNIVERSE_SIZE * SIZE_MULTIPLIER));
    }

    @Test
    public void fixedAmount() {
        given(properties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getSizeMultiplier()).willReturn(CollectionUtils.singleValueMap(SystemAmount.SMALL, SIZE_MULTIPLIER));

        int result = underTest.getMaxAllocationTryCount(UNIVERSE_SIZE, SystemAmount.SMALL);

        assertThat(result).isEqualTo((int) (UNIVERSE_SIZE * SIZE_MULTIPLIER));
    }
}