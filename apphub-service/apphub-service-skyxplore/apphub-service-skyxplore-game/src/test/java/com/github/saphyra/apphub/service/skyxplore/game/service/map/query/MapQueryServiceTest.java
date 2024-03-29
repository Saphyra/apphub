package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MapQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private UniverseToMapConverter universeToMapConverter;

    @InjectMocks
    private MapQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private MapResponse mapResponse;

    @Test
    public void getMap() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(universeToMapConverter.convert(USER_ID, gameData)).willReturn(mapResponse);

        MapResponse result = underTest.getMap(USER_ID);

        assertThat(result).isEqualTo(mapResponse);
    }
}