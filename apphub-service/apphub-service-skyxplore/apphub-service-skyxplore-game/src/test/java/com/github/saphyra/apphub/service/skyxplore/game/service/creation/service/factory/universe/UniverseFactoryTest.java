package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system.SolarSystemPlacingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection.SystemConnectionProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UniverseFactoryTest {
    private static final int MEMBER_NUM = 234;
    private static final Integer UNIVERSE_SIZE = 2435;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private UniverseSizeCalculator universeSizeCalculator;

    @Mock
    private SolarSystemPlacingService starSystemFactory;

    @Mock
    private SystemConnectionProvider systemConnectionProvider;

    @InjectMocks
    private UniverseFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SystemConnection systemConnection;

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(UniverseSize.SMALL)
            .build();

        given(universeSizeCalculator.calculate(MEMBER_NUM, UniverseSize.SMALL)).willReturn(UNIVERSE_SIZE);
        given(systemConnectionProvider.getConnections(GAME_ID, CollectionUtils.toSet(coordinate))).willReturn(Arrays.asList(systemConnection));

        given(starSystemFactory.create(GAME_ID, MEMBER_NUM, UNIVERSE_SIZE, settings)).willReturn(CollectionUtils.singleValueMap(coordinate, solarSystem));

        Universe result = underTest.create(GAME_ID, MEMBER_NUM, settings);

        assertThat(result.getSize()).isEqualTo(UNIVERSE_SIZE);
        assertThat(result.getSystems()).containsEntry(coordinate, solarSystem);
        assertThat(result.getConnections()).containsExactly(systemConnection);
    }
}