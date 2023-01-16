package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SystemConnectionToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();

    @InjectMocks
    private SystemConnectionToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private LineModelWrapper line;

    @Mock
    private LineModel lineModel;

    @Mock
    private CoordinateModel coordinateModel1;

    @Mock
    private CoordinateModel coordinateModel2;

    @Test
    public void convert() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(line.getModel()).willReturn(lineModel);
        given(line.getA()).willReturn(coordinateModel1);
        given(line.getB()).willReturn(coordinateModel2);

        SystemConnection connection = SystemConnection.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID)
            .line(line)
            .build();

        List<GameItem> result = underTest.convert(Arrays.asList(connection), game);

        SystemConnectionModel expected = new SystemConnectionModel();
        expected.setId(SYSTEM_CONNECTION_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.SYSTEM_CONNECTION);

        assertThat(result).containsExactlyInAnyOrder(expected, lineModel, coordinateModel1, coordinateModel2);
    }
}