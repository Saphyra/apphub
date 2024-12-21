package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

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
class ConstructionAreaFactoryTest {
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ConstructionAreaFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.create(LOCATION, SURFACE_ID, DATA_ID))
            .returns(CONSTRUCTION_AREA_ID, ConstructionArea::getConstructionAreaId)
            .returns(LOCATION, ConstructionArea::getLocation)
            .returns(SURFACE_ID, ConstructionArea::getSurfaceId)
            .returns(DATA_ID, ConstructionArea::getDataId);
    }
}