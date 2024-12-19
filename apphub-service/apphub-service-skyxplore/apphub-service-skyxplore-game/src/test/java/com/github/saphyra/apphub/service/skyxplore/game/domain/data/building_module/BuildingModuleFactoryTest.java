package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BuildingModuleFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(BUILDING_MODULE_ID);

        assertThat(underTest.create(LOCATION, CONSTRUCTION_AREA_ID, DATA_ID))
            .returns(BUILDING_MODULE_ID, BuildingModule::getBuildingModuleId)
            .returns(LOCATION, BuildingModule::getLocation)
            .returns(CONSTRUCTION_AREA_ID, BuildingModule::getConstructionAreaId)
            .returns(DATA_ID, BuildingModule::getDataId);
    }
}