package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemPlacingServiceTest {
    private static final int MEMBER_NUM = 425;
    private static final int UNIVERSE_SIZE = 345;
    private static final String SYSTEM_NAME = "system-name";

    @Mock
    private SolarSystemCoordinateProvider solarSystemCoordinateProvider;

    @Mock
    private SolarSystemNames solarSystemNames;

    private ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(new SleepService());

    @Mock
    private SolarSystemFactory solarSystemFactory;

    private SolarSystemPlacingService underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Before
    public void setUp() {
        underTest = SolarSystemPlacingService.builder()
            .solarSystemCoordinateProvider(solarSystemCoordinateProvider)
            .solarSystemNames(solarSystemNames)
            .solarSystemFactory(solarSystemFactory)
            .executorServiceBean(executorServiceBean)
            .build();
    }

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .systemAmount(SystemAmount.RANDOM)
            .build();

        given(solarSystemCoordinateProvider.getCoordinates(MEMBER_NUM, UNIVERSE_SIZE, SystemAmount.RANDOM)).willReturn(Arrays.asList(coordinate));
        given(solarSystemNames.getRandomStarName(Collections.emptyList())).willReturn(SYSTEM_NAME);
        given(solarSystemFactory.create(settings, SYSTEM_NAME, coordinate)).willReturn(solarSystem);
        given(solarSystem.getCoordinate()).willReturn(coordinate);

        Map<Coordinate, SolarSystem> result = underTest.create(MEMBER_NUM, UNIVERSE_SIZE, settings);

        assertThat(result).containsEntry(coordinate, solarSystem);
    }
}