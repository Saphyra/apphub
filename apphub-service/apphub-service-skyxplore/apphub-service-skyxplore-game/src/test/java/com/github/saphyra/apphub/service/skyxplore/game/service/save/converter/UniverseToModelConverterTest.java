package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UniverseToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int SIZE = 1342;

    @Mock
    private SystemConnectionToModelConverter connectionConverter;

    @Mock
    private SolarSystemToModelConverter solarSystemConverter;

    @InjectMocks
    private UniverseToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private SystemConnection systemConnection;

    @Mock
    private SystemConnectionModel systemConnectionModel;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SolarSystemModel solarSystemModel;

    @Test
    public void convertDeep() {
        given(game.getGameId()).willReturn(GAME_ID);

        given(connectionConverter.convert(Arrays.asList(systemConnection), game)).willReturn(Arrays.asList(systemConnectionModel));
        given(solarSystemConverter.convertDeep(any(), eq(game))).willReturn(Arrays.asList(solarSystemModel));

        Universe universe = Universe.builder()
            .size(SIZE)
            .systems(CollectionUtils.singleValueMap(new Coordinate(0, 0), solarSystem))
            .connections(Arrays.asList(systemConnection))
            .build();

        List<GameItem> result = underTest.convertDeep(universe, game);

        UniverseModel expected = new UniverseModel();
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.UNIVERSE);
        expected.setSize(SIZE);

        assertThat(result).containsExactlyInAnyOrder(solarSystemModel, systemConnectionModel, expected);
    }
}