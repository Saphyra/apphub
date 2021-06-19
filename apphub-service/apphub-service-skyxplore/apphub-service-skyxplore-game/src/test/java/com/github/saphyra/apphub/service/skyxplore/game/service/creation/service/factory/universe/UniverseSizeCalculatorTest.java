package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
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
public class UniverseSizeCalculatorTest {
    private static final int MEMBER_NUM = 2;
    private static final Integer BASE_SIZE = 100;
    private static final Double MIN_MULTIPLIER = 312421d;
    private static final Double MAX_MULTIPLIER = 4325234523d;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private Random random;

    @InjectMocks
    private UniverseSizeCalculator underTest;

    @Mock
    private GameCreationProperties.UniverseProperties universeProperties;

    @Test
    public void calculate() {
        given(properties.getUniverse()).willReturn(universeProperties);

        given(universeProperties.getBaseSize()).willReturn(BASE_SIZE);
        given(universeProperties.getMemberMultiplier()).willReturn(3d);
        given(universeProperties.getSettingMultiplier()).willReturn(CollectionUtils.singleValueMap(UniverseSize.SMALL, 2d));
        given(universeProperties.getMinMultiplier()).willReturn(MIN_MULTIPLIER);
        given(universeProperties.getMaxMultiplier()).willReturn(MAX_MULTIPLIER);
        given(random.randDouble(MIN_MULTIPLIER, MAX_MULTIPLIER)).willReturn(3d);

        int result = underTest.calculate(MEMBER_NUM, UniverseSize.SMALL);

        assertThat(result).isEqualTo(100 * 9 * 2 * 3);
    }
}