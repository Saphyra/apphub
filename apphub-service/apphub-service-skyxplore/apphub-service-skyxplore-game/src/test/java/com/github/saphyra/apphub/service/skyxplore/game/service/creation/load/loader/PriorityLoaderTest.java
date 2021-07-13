package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PriorityLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PRIORITY = 253;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private PriorityLoader underTest;

    @Mock
    private PriorityModel priorityModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.PRIORITY, PriorityModel[].class)).willReturn(Arrays.asList(priorityModel));
        given(priorityModel.getPriorityType()).willReturn(PriorityType.CONSTRUCTION.name());
        given(priorityModel.getValue()).willReturn(PRIORITY);

        Map<PriorityType, Integer> result = underTest.load(LOCATION);

        assertThat(result).containsEntry(PriorityType.CONSTRUCTION, PRIORITY);
    }
}
