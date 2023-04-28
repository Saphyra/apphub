package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous.MiscellaneousBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameDataControllerImplTest {
    private static final String DATA_ID = "data-id";
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final String MISC_BUILDING_DATA_ID = "misc-building-data-id";

    private final AbstractDataService<String, GameDataItem> dataService = new DummyDataService();

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    private GameDataControllerImpl underTest;

    @Mock
    private GameDataItem gameDataItem;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @BeforeEach
    public void setUp() {
        given(gameDataItem.getId()).willReturn(DATA_ID);
        dataService.put(DATA_ID, gameDataItem);

        underTest = new GameDataControllerImpl(Arrays.asList(dataService), terraformingPossibilitiesService);
    }

    @Test
    public void dataNotFound() {
        Throwable ex = catchThrowable(() -> underTest.getGameData("asd"));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void dataFound() {
        Object result = underTest.getGameData(DATA_ID);

        assertThat(result).isEqualTo(gameDataItem);
    }

    @Test
    public void availableBuildings_invalidSurfaceType() {
        Throwable ex = catchThrowable(() -> underTest.getAvailableBuildings("asd"));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "invalid value");
    }

    @Test
    public void availableBuildings() {
        BuildingData buildingData = new StorageBuildingData();
        buildingData.setId(BUILDING_DATA_ID);

        MiscellaneousBuilding miscellaneousBuilding = new MiscellaneousBuilding();
        miscellaneousBuilding.setId(MISC_BUILDING_DATA_ID);
        miscellaneousBuilding.setPlaceableSurfaceTypes(List.of(SurfaceType.LAKE));

        GameDataItem gameDataItem = new ResourceData();

        dataService.put(BUILDING_DATA_ID, buildingData);
        dataService.put(MISC_BUILDING_DATA_ID, miscellaneousBuilding);
        dataService.put(DATA_ID, gameDataItem);

        underTest = new GameDataControllerImpl(List.of(dataService), terraformingPossibilitiesService);

        List<String> result = underTest.getAvailableBuildings(SurfaceType.CONCRETE.name());

        assertThat(result).containsExactly(BUILDING_DATA_ID);
    }

    @Test
    public void getTerraformingPossibilities_surfaceTypeCannotBeTerraformed() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.CONCRETE)).willReturn(Optional.empty());

        List<Object> result = underTest.getTerraformingPossibilities(SurfaceType.CONCRETE.name());

        assertThat(result).isEmpty();
    }

    @Test
    public void getTerraformingPossibilities() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.CONCRETE)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));

        List<Object> result = underTest.getTerraformingPossibilities(SurfaceType.CONCRETE.name());

        assertThat(result).containsExactly(terraformingPossibility);
    }

    private static class DummyDataService extends AbstractDataService<String, GameDataItem> {

        public DummyDataService() {
            super(null, null);
        }

        @Override
        public void init() {

        }

        @Override
        public void addItem(GameDataItem content, String fileName) {

        }
    }
}