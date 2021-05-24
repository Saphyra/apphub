package com.github.saphyra.apphub.service.skyxplore.data.save_game.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.data.common.LineConverter;
import com.github.saphyra.apphub.service.skyxplore.data.common.LineEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SystemConnectionConverterTest {
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String SYSTEM_CONNECTION_ID_STRING = "system-connection-id";
    private static final String GAME_ID_STRING = "game-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LineConverter lineConverter;

    @InjectMocks
    private SystemConnectionConverter underTest;

    @Mock
    private Line line;

    @Mock
    private LineEntity lineEntity;

    @Test
    public void convertDomain() {
        SystemConnectionModel model = new SystemConnectionModel();
        model.setId(SYSTEM_CONNECTION_ID);
        model.setGameId(GAME_ID);
        model.setLine(line);

        given(uuidConverter.convertDomain(SYSTEM_CONNECTION_ID)).willReturn(SYSTEM_CONNECTION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(lineConverter.convertDomain(line, SYSTEM_CONNECTION_ID)).willReturn(lineEntity);

        SystemConnectionEntity result = underTest.convertDomain(model);

        assertThat(result.getSystemConnectionId()).isEqualTo(SYSTEM_CONNECTION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLine()).isEqualTo(lineEntity);
    }

    @Test
    public void convertEntity() {
        SystemConnectionEntity entity = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_STRING)
            .gameId(GAME_ID_STRING)
            .line(lineEntity)
            .build();

        given(uuidConverter.convertEntity(SYSTEM_CONNECTION_ID_STRING)).willReturn(SYSTEM_CONNECTION_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(lineConverter.convertEntity(lineEntity)).willReturn(line);

        SystemConnectionModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SYSTEM_CONNECTION);
        assertThat(result.getLine()).isEqualTo(line);
    }
}