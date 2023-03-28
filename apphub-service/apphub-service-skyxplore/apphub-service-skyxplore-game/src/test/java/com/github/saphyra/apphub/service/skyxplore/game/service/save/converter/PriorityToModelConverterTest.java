package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
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
    private static final Integer PRIORITY_VALUE = 32;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PRIORITY_ID = UUID.randomUUID();

    @InjectMocks
    private PriorityToModelConverter underTest;

    @Mock
    private Priority priority;

    @Test
    public void convert() {
        given(priority.getPriorityId()).willReturn(PRIORITY_ID);
        given(priority.getLocation()).willReturn(LOCATION);
        given(priority.getValue()).willReturn(PRIORITY_VALUE);
        given(priority.getType()).willReturn(PriorityType.CONSTRUCTION);

        List<PriorityModel> result = underTest.convert(GAME_ID, List.of(priority));

        assertThat(result.get(0).getId()).isEqualTo(PRIORITY_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.PRIORITY);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
        assertThat(result.get(0).getValue()).isEqualTo(PRIORITY_VALUE);
        assertThat(result.get(0).getPriorityType()).isEqualTo(PriorityType.CONSTRUCTION.name());
    }
}