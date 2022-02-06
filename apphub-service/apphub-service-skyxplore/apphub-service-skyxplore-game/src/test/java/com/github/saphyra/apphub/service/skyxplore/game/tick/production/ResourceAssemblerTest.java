package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ResourceAssemblerTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ASSIGNEE = UUID.randomUUID();

    @Mock
    private ProductionBuildingOrderProcessor productionBuildingOrderProcessor;

    @InjectMocks
    private ResourceAssembler underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder order;

    @Mock
    private Building building;

    @Test
    public void buildingNotFound() {
        given(planet.getBuildings()).willReturn(List.of(building));
        given(building.getBuildingId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.assembleResource(GAME_ID, planet, order));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void assembleResource() {
        given(planet.getBuildings()).willReturn(List.of(building));
        given(building.getBuildingId()).willReturn(ASSIGNEE);
        given(order.getAssignee()).willReturn(ASSIGNEE);

        underTest.assembleResource(GAME_ID, planet, order);

        verify(productionBuildingOrderProcessor).processOrderByAssignedBuilding(GAME_ID, planet, building, order);
    }
}