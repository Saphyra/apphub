package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PriorityToModelConverterTest {
    private static final Integer PRIORITY = 32;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private PriorityToModelConverter underTest;

    @Mock
    private Game game;

    @Test
    public void convert() {
        given(game.getGameId()).willReturn(GAME_ID);

        List<PriorityModel> result = underTest.convert(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, PRIORITY), LOCATION, LocationType.PLANET, game);

        assertThat(result.get(0).getId()).isNull();
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.PRIORITY);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
        assertThat(result.get(0).getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.get(0).getValue()).isEqualTo(PRIORITY);
        assertThat(result.get(0).getPriorityType()).isEqualTo(PriorityType.CONSTRUCTION.name());
    }
}