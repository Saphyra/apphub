package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructionAreaLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ConstructionAreaLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.CONSTRUCTION_AREA);
    }

    @Test
    void addToGameData() {
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);

        underTest.addToGameData(gameData, List.of(constructionArea));

        then(constructionAreas).should().addAll(List.of(constructionArea));
    }

    @Test
    void convert() {
        ConstructionAreaModel model = new ConstructionAreaModel();
        model.setId(CONSTRUCTION_AREA_ID);
        model.setLocation(LOCATION);
        model.setSurfaceId(SURFACE_ID);
        model.setDataId(DATA_ID);

        assertThat(underTest.convert(model))
            .returns(CONSTRUCTION_AREA_ID, ConstructionArea::getConstructionAreaId)
            .returns(LOCATION, ConstructionArea::getLocation)
            .returns(SURFACE_ID, ConstructionArea::getSurfaceId)
            .returns(DATA_ID, ConstructionArea::getDataId);
    }
}