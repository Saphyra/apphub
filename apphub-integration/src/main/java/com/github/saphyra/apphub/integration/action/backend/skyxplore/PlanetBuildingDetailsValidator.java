package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.core.ForRemoval;
import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetBuildingOverviewResponse;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PlanetBuildingDetailsValidator {
    @ForRemoval("skyxplore-react")
    public static void verifyBuildingDetails(Object buildingDetailsObj, String surfaceType, String buildingDataId, int numberOfBuildings, int levelOfBuildings) {
        TypeReference<Map<String, PlanetBuildingOverviewResponse>> typeReference = new TypeReference<>() {
        };

        Map<String, PlanetBuildingOverviewResponse> buildingDetails = TestBase.OBJECT_MAPPER_WRAPPER.convertValue(buildingDetailsObj, typeReference);

        verifyBuildingDetails(buildingDetails, surfaceType, buildingDataId, numberOfBuildings, levelOfBuildings);
    }

    public static void verifyBuildingDetails(Map<String, PlanetBuildingOverviewResponse> buildingDetails, String surfaceType, String buildingDataId, int numberOfBuildings, int levelOfBuildings) {
        assertThat(buildingDetails).isNotNull();

        PlanetBuildingOverviewResponse response = buildingDetails.get(surfaceType);

        Optional<PlanetBuildingOverviewDetailedResponse> maybeBuildingDetail = response.getBuildingDetails()
            .stream()
            .filter(building -> building.getDataId().equals(buildingDataId))
            .findFirst();

        if (numberOfBuildings == 0) {
            assertThat(maybeBuildingDetail).isEmpty();
        } else {
            PlanetBuildingOverviewDetailedResponse buildingDetail = maybeBuildingDetail.orElseThrow(() -> new RuntimeException("Building not found with id " + buildingDataId + " on surfaceType " + surfaceType));

            assertThat(buildingDetail.getLevelSum()).isEqualTo(levelOfBuildings);
            assertThat(buildingDetail.getUsedSlots()).isEqualTo(numberOfBuildings);
        }
    }
}
