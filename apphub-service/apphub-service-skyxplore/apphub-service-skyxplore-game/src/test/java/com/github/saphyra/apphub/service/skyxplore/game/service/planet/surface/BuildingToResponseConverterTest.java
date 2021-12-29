package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingToResponseConverterTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int LEVEL = 432;

    @Mock
    private ConstructionToResponseConverter constructionToResponseConverter;

    @InjectMocks
    private BuildingToResponseConverter underTest;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionResponse constructionResponse;

    @Test
    public void convertNull() {
        assertThat(underTest.convert(null)).isNull();
    }

    @Test
    public void convert() {
        Building building = Building.builder()
            .buildingId(BUILDING_ID)
            .dataId(DATA_ID)
            .level(LEVEL)
            .construction(construction)
            .build();
        given(constructionToResponseConverter.convert(construction)).willReturn(constructionResponse);

        SurfaceBuildingResponse result = underTest.convert(building);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getConstruction()).isEqualTo(constructionResponse);
    }
}