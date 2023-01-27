package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
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

@ExtendWith(MockitoExtension.class)
public class ConstructionLoaderTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2456;
    private static final Integer CURRENT_WORK_POINTS = 234;
    private static final Integer PRIORITY = 564;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA = "data";
    private static final Integer PARALLEL_WORKERS = 245;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ConstructionLoader underTest;

    @Test
    public void load() {
        ConstructionModel model = new ConstructionModel();
        model.setId(CONSTRUCTION_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        model.setPriority(PRIORITY);
        model.setData(DATA);
        model.setParallelWorkers(PARALLEL_WORKERS);

        given(gameItemLoader.loadChildren(BUILDING_ID, GameItemType.CONSTRUCTION, ConstructionModel[].class)).willReturn(List.of(model));

        Construction result = underTest.load(BUILDING_ID);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getParallelWorkers()).isEqualTo(PARALLEL_WORKERS);
    }
}