package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SystemConnectionConverterTest {
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String SYSTEM_CONNECTION_ID_STRING = "system-connection-id";
    private static final String GAME_ID_STRING = "game-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SystemConnectionConverter underTest;

    @Test
    public void convertDomain() {
        SystemConnectionModel model = new SystemConnectionModel();
        model.setId(SYSTEM_CONNECTION_ID);
        model.setGameId(GAME_ID);

        given(uuidConverter.convertDomain(SYSTEM_CONNECTION_ID)).willReturn(SYSTEM_CONNECTION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        SystemConnectionEntity result = underTest.convertDomain(model);

        assertThat(result.getSystemConnectionId()).isEqualTo(SYSTEM_CONNECTION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
    }

    @Test
    public void convertEntity() {
        SystemConnectionEntity entity = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_STRING)
            .gameId(GAME_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(SYSTEM_CONNECTION_ID_STRING)).willReturn(SYSTEM_CONNECTION_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);

        SystemConnectionModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SYSTEM_CONNECTION);
    }
}