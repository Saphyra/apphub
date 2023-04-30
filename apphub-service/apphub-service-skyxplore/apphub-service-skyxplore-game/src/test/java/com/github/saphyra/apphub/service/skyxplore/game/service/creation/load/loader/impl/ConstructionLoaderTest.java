package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
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
class ConstructionLoaderTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PARALLEL_WORKERS = 43265;
    private static final Integer REQUIRED_WORK_POINTS = 457;
    private static final String DATA = "data";
    private static final Integer CURRENT_WORK_POINTS = 3645;
    private static final Integer PRIORITY = 3547;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ConstructionLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.CONSTRUCTION);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(ConstructionModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getConstructions()).willReturn(constructions);

        underTest.addToGameData(gameData, List.of(construction));

        verify(constructions).addAll(List.of(construction));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(CONSTRUCTION_ID);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getConstructionType()).willReturn(ConstructionType.CONSTRUCTION);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(model.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(model.getData()).willReturn(DATA);
        given(model.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(model.getPriority()).willReturn(PRIORITY);

        Construction result = underTest.convert(model);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getConstructionType()).isEqualTo(ConstructionType.CONSTRUCTION);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getParallelWorkers()).isEqualTo(PARALLEL_WORKERS);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}