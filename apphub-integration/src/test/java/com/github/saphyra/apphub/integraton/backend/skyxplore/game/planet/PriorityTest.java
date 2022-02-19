package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePriorityActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.StringIntMap;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
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

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId1);

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

        ApphubWsClient.cleanUpConnections();
    }
}
