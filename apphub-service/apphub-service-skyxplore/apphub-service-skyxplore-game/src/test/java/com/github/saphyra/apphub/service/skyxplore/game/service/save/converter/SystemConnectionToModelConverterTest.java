package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SystemConnectionToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();

    @InjectMocks
    private SystemConnectionToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Line line;

    @Test
    public void convert() {
        given(game.getGameId()).willReturn(GAME_ID);

        SystemConnection connection = SystemConnection.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID)
            .line(line)
            .build();

        List<SystemConnectionModel> result = underTest.convert(Arrays.asList(connection), game);

        assertThat(result.get(0).getId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.SYSTEM_CONNECTION);
        assertThat(result.get(0).getLine()).isEqualTo(line);
    }
}