package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PriorityLoaderTest {
    private static final UUID PRIORITY_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer VALUE = 3426;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private PriorityLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private PriorityModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.PRIORITY);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(PriorityModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getPriorities()).willReturn(priorities);

        underTest.addToGameData(gameData, List.of(priority));

        verify(priorities).addAll(List.of(priority));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(PRIORITY_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getPriorityType()).willReturn(PriorityType.CONSTRUCTION.name());
        given(model.getValue()).willReturn(VALUE);

        Priority result = underTest.convert(model);

        assertThat(result.getPriorityId()).isEqualTo(PRIORITY_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getType()).isEqualTo(PriorityType.CONSTRUCTION);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }
}