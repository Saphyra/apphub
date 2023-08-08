package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingDetailMapperTest {
    private static final String DATA_ID = "data-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @InjectMocks
    private BuildingDetailMapper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building1;

    @Mock
    private Building building2;

    @Mock
    private Building deconstructedBuilding;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Test
    public void createBuildingDetail() {
        given(deconstructedBuilding.getBuildingId()).willReturn(BUILDING_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));
        given(deconstructions.findByExternalReference(null)).willReturn(Optional.empty());

        given(building1.getLevel()).willReturn(1);
        given(building2.getLevel()).willReturn(3);

        PlanetBuildingOverviewDetailedResponse result = underTest.createBuildingDetail(gameData, DATA_ID, Arrays.asList(building1, building2, deconstructedBuilding));

        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevelSum()).isEqualTo(4);
        assertThat(result.getUsedSlots()).isEqualTo(3);
    }
}