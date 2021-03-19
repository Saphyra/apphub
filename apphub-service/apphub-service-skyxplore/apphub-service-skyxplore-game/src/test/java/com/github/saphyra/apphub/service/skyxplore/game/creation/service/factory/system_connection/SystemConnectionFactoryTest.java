package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Line;
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
    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SystemConnectionFactory underTest;

    @Mock
    private Line line;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(SYSTEM_CONNECTION_ID);

        SystemConnection result = underTest.create(line);

        assertThat(result.getSystemConnectionId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.getLine()).isEqualTo(line);
    }

}