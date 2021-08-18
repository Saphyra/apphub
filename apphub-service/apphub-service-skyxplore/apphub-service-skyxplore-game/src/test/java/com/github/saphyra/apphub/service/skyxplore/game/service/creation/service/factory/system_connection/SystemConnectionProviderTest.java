package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SystemConnectionProviderTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @Mock
    private AllSystemsConnectionProvider allSystemsConnectionProvider;

    @Mock
    private CrossRemovalService crossRemovalService;

    @Mock
    private TooShortConnectionRemovalService tooShortConnectionRemovalService;

    @Mock
    private ConnectionOverflowRemovalService connectionOverflowRemovalService;

    @Mock
    private LonelySystemConnectionService lonelySystemConnectionService;

    @Mock
    private SystemConnectionFactory systemConnectionFactory;

    @InjectMocks
    private SystemConnectionProvider underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Line line1;

    @Mock
    private Line line2;

    @Mock
    private Line line3;

    @Mock
    private Line line4;

    @Mock
    private Line line5;

    @Mock
    private SystemConnection systemConnection1;

    @Mock
    private SystemConnection systemConnection2;

    @Test
    public void getConnections() {
        given(allSystemsConnectionProvider.connectToAllSystems(coordinate, Arrays.asList(coordinate))).willReturn(Arrays.asList(line1, line2, line3, line4));
        given(crossRemovalService.removeCrosses(Arrays.asList(line1, line2, line3, line4))).willReturn(Arrays.asList(line1, line2, line3));
        given(tooShortConnectionRemovalService.filterLinesTooCloseToASystem(Arrays.asList(coordinate), Arrays.asList(line1, line2, line3))).willReturn(Arrays.asList(line1, line2));
        given(connectionOverflowRemovalService.removeConnectionOverflow(Arrays.asList(coordinate), Arrays.asList(line1, line2))).willReturn(Arrays.asList(line1));
        given(lonelySystemConnectionService.connectLonelySystems(Arrays.asList(coordinate), Arrays.asList(line1))).willReturn(Arrays.asList(line1, line5));

        given(systemConnectionFactory.create(GAME_ID, line1)).willReturn(systemConnection1);
        given(systemConnectionFactory.create(GAME_ID, line5)).willReturn(systemConnection2);

        List<SystemConnection> result = underTest.getConnections(GAME_ID, Arrays.asList(coordinate));

        assertThat(result).containsExactlyInAnyOrder(systemConnection1, systemConnection2);
    }
}