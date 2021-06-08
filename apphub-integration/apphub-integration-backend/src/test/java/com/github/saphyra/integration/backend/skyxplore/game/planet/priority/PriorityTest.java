package com.github.saphyra.integration.backend.skyxplore.game.planet.priority;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePriorityActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.StringIntMap;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PriorityTest extends BackEndTest {
    private static final String GAME_NAME = "game";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void testPriorities(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        //Get
        Response getResponse = SkyXplorePriorityActions.getPrioritiesResponse(language, accessTokenId1, planet.getPlanetId());
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        Map<String, Integer> priorities = getResponse.getBody().as(StringIntMap.class);
        assertThat(priorities).hasSize(PriorityType.values().length);
        Arrays.stream(PriorityType.values())
            .forEach(priorityType -> assertThat(priorities).containsEntry(priorityType.name().toLowerCase(), 5));

        //Unknown value
        Response unknownValueResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(language, accessTokenId1, planet.getPlanetId(), "asfaed", 4);
        verifyInvalidParam(language, unknownValueResponse, "priorityType", "unknown value");

        //Too low
        Response tooLowResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(language, accessTokenId1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 0);
        verifyInvalidParam(language, tooLowResponse, "priority", "too low");

        //Too high
        Response tooHighResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(language, accessTokenId1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 11);
        verifyInvalidParam(language, tooHighResponse, "priority", "too high");

        //Update
        Response updateResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(language, accessTokenId1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 7);
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);
        Map<String, Integer> modifiedPriorities = SkyXplorePriorityActions.getPriorities(language, accessTokenId1, planet.getPlanetId());
        assertThat(modifiedPriorities).containsEntry(PriorityType.CONSTRUCTION.name().toLowerCase(), 7);
    }

    private void verifyInvalidParam(Language language, Response response, String field, String value) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry(field, value);
    }
}
