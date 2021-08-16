package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemPlacingServiceTest {
    private static final int MEMBER_NUM = 425;
    private static final int UNIVERSE_SIZE = 345;
    private static final String SYSTEM_NAME = "system-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SolarSystemCoordinateProvider solarSystemCoordinateProvider;

    @Mock
    private SolarSystemNames solarSystemNames;

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @Mock
    private SolarSystemFactory solarSystemFactory;

    @InjectMocks
    private SolarSystemPlacingService underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .systemAmount(SystemAmount.RANDOM)
            .build();

        given(solarSystemCoordinateProvider.getCoordinates(MEMBER_NUM, UNIVERSE_SIZE, SystemAmount.RANDOM)).willReturn(Arrays.asList(coordinate));
        given(solarSystemNames.getRandomStarName(Collections.emptyList())).willReturn(SYSTEM_NAME);
        given(solarSystemFactory.create(GAME_ID, settings, SYSTEM_NAME, coordinate)).willReturn(solarSystem);
        given(solarSystem.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        Map<Coordinate, SolarSystem> result = underTest.create(GAME_ID, MEMBER_NUM, UNIVERSE_SIZE, settings);

        assertThat(result).containsEntry(coordinate, solarSystem);
    }
}