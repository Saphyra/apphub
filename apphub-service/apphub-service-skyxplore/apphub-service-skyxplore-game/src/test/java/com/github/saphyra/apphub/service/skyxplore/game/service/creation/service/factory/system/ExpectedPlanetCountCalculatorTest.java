package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ExpectedPlanetCountCalculatorTest {
    private static final int PLAYER_COUNT = 10;
    private static final Double MIN_AMOUNT_MULTIPLIER = 2354d;
    private static final Double MAX_AMOUNT_MULTIPLIER = 567d;
    private static final Double AMOUNT_MULTIPLIER = 23d;

    @Mock
    private GameCreationProperties gameCreationProperties;

    @Mock
    private Random random;

    @InjectMocks
    private ExpectedPlanetCountCalculator underTest;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @Mock
    private GameCreationProperties.SolarSystemProperties solarSystemProperties;

    @Test
    public void calculateExpectedPlanetCount() {
        given(settings.getSystemAmount()).willReturn(SystemAmount.COMMON);
        given(gameCreationProperties.getSolarSystem()).willReturn(solarSystemProperties);
        given(solarSystemProperties.getAmountMultiplier()).willReturn(CollectionUtils.singleValueMap(SystemAmount.COMMON, new Range<>(MIN_AMOUNT_MULTIPLIER, MAX_AMOUNT_MULTIPLIER)));
        given(random.randDouble(MIN_AMOUNT_MULTIPLIER, MAX_AMOUNT_MULTIPLIER)).willReturn(AMOUNT_MULTIPLIER);

        int result = underTest.calculateExpectedPlanetCount(PLAYER_COUNT, settings);

        assertThat(result).isEqualTo((int) (PLAYER_COUNT * AMOUNT_MULTIPLIER));
    }
}