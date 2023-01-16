package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingLoaderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 42;

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private ConstructionLoader constructionLoader;

    @InjectMocks
    private BuildingLoader underTest;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private Construction construction;

    @Test
    public void load_notFound() {
        assertThat(underTest.load(SURFACE_ID)).isNull();
    }

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(SURFACE_ID, GameItemType.BUILDING, BuildingModel[].class)).willReturn(Arrays.asList(buildingModel));
        given(constructionLoader.load(BUILDING_ID)).willReturn(construction);

        given(buildingModel.getId()).willReturn(BUILDING_ID);
        given(buildingModel.getSurfaceId()).willReturn(SURFACE_ID);
        given(buildingModel.getDataId()).willReturn(DATA_ID);
        given(buildingModel.getLevel()).willReturn(LEVEL);

        Building result = underTest.load(SURFACE_ID);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getConstruction()).isEqualTo(construction);
    }
}