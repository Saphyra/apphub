package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BuildingControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private CancelConstructionService cancelConstructionService;

    @Mock
    private CancelDeconstructionService cancelDeconstructionService;

    @InjectMocks
    private BuildingController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void cancelConstruction() {
        underTest.cancelConstruction(PLANET_ID, BUILDING_ID, accessTokenHeader);

        verify(cancelConstructionService).cancelConstructionOfBuilding(USER_ID, BUILDING_ID);
    }

    @Test
    void cancelDeconstruction() {
        underTest.cancelDeconstruction(PLANET_ID, BUILDING_ID, accessTokenHeader);

        verify(cancelDeconstructionService).cancelDeconstructionOfBuilding(USER_ID, BUILDING_ID);
    }
}