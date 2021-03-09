package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemCoordinateProviderTest {
    private static final int UNIVERSE_SIZE = 3145;
    private static final int MAX_ALLOCATION_TRY_COUNT = 10;
    private static final int MEMBER_NUM = 1;
    private static final Integer ALLOCATION_TRY_COUNT = 5;

    @Mock
    private Random random;

    @Mock
    private SolarSystemCoordinateListProvider solarSystemCoordinateListProvider;

    @Mock
    private MaxSystemCountCalculator maxSystemCountCalculator;

    @InjectMocks
    private SolarSystemCoordinateProvider underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Test
    public void getCoordinates() {
        given(maxSystemCountCalculator.getMaxAllocationTryCount(UNIVERSE_SIZE, SystemAmount.RANDOM)).willReturn(MAX_ALLOCATION_TRY_COUNT);
        given(random.randInt(MAX_ALLOCATION_TRY_COUNT / 2, MAX_ALLOCATION_TRY_COUNT)).willReturn(ALLOCATION_TRY_COUNT);
        given(solarSystemCoordinateListProvider.getCoordinates(UNIVERSE_SIZE, ALLOCATION_TRY_COUNT)).willReturn(Arrays.asList(coordinate1))
            .willReturn(Arrays.asList(coordinate1, coordinate2));

        List<Coordinate> result = underTest.getCoordinates(MEMBER_NUM, UNIVERSE_SIZE, SystemAmount.RANDOM);

        assertThat(result).containsExactly(coordinate1, coordinate2);
    }

    @Test(expected = RuntimeException.class)
    public void getCoordinates_couldNotPlaceEnough() {
        given(maxSystemCountCalculator.getMaxAllocationTryCount(UNIVERSE_SIZE, SystemAmount.RANDOM)).willReturn(MAX_ALLOCATION_TRY_COUNT);
        given(random.randInt(MAX_ALLOCATION_TRY_COUNT / 2, MAX_ALLOCATION_TRY_COUNT)).willReturn(ALLOCATION_TRY_COUNT);
        given(solarSystemCoordinateListProvider.getCoordinates(UNIVERSE_SIZE, ALLOCATION_TRY_COUNT)).willReturn(Arrays.asList(coordinate1));

        underTest.getCoordinates(MEMBER_NUM, UNIVERSE_SIZE, SystemAmount.RANDOM);
    }
}