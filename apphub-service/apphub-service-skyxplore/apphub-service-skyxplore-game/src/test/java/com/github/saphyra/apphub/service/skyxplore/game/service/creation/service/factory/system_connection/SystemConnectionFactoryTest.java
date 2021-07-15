package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapperFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SystemConnectionFactoryTest {
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private LineModelWrapperFactory lineModelWrapperFactory;

    @InjectMocks
    private SystemConnectionFactory underTest;

    @Mock
    private Line line;

    @Mock
    private LineModelWrapper lineModelWrapper;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(SYSTEM_CONNECTION_ID);
        given(lineModelWrapperFactory.create(line, GAME_ID, SYSTEM_CONNECTION_ID)).willReturn(lineModelWrapper);

        SystemConnection result = underTest.create(GAME_ID, line);

        assertThat(result.getSystemConnectionId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.getLine()).isEqualTo(lineModelWrapper);
    }
}