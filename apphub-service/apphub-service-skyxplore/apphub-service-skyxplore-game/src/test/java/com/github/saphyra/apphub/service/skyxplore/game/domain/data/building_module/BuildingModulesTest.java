package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModulesTest {
    private static final UUID LOCATION_1 = UUID.randomUUID();
    private static final UUID LOCATION_2 = UUID.randomUUID();
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final UUID CONSTRUCTION_AREA_ID_1 = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID_2 = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID_1 = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID_2 = UUID.randomUUID();

    @Mock
    private BuildingModule buildingModule1;

    @Mock
    private BuildingModule buildingModule2;

    @Mock
    private BuildingModule buildingModule3;

    @Test
    void getByLocation() {
        given(buildingModule1.getLocation()).willReturn(LOCATION_1);
        given(buildingModule2.getLocation()).willReturn(LOCATION_2);

        assertThat(new BuildingModules(buildingModule1, buildingModule2).getByLocation(LOCATION_1)).containsExactly(buildingModule1);
    }

    @Test
    void getByLocationAndDataId() {
        given(buildingModule1.getLocation()).willReturn(LOCATION_1);
        given(buildingModule1.getDataId()).willReturn(DATA_ID_1);

        given(buildingModule2.getLocation()).willReturn(LOCATION_2);

        given(buildingModule3.getLocation()).willReturn(LOCATION_1);
        given(buildingModule3.getDataId()).willReturn(DATA_ID_2);

        assertThat(new BuildingModules(buildingModule1, buildingModule2, buildingModule3).getByLocationAndDataId(LOCATION_1, DATA_ID_1)).containsExactly(buildingModule1);
    }

    @Test
    void getByConstructionAreaId() {
        given(buildingModule1.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID_1);
        given(buildingModule2.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID_2);

        assertThat(new BuildingModules(buildingModule1, buildingModule2).getByConstructionAreaId(CONSTRUCTION_AREA_ID_1)).containsExactly(buildingModule1);
    }

    @Test
    void findByBuildingModuleIdValidated_notFound() {
        given(buildingModule1.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID_1);

        ExceptionValidator.validateNotLoggedException(() -> new BuildingModules(buildingModule1).findByIdValidated(BUILDING_MODULE_ID_2), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByBuildingModuleIdValidated() {
        given(buildingModule1.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID_1);

        assertThat(new BuildingModules(buildingModule1).findByIdValidated(BUILDING_MODULE_ID_1)).isEqualTo(buildingModule1);
    }
}