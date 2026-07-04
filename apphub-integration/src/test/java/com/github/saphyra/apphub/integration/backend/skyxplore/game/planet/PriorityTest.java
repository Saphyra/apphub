package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePriorityActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
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

    @Test(groups = {"be", "skyxplore"})
    public void testPriorities() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken1, userId1))
            .get(accessToken1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken1);

        get(accessToken1, planet);
        unknownValue(accessToken1, planet);
        tooLow(accessToken1, planet);
        tooHigh(accessToken1, planet);
        update(accessToken1, planet);
    }

    private static void get(String accessToken1, PlanetLocationResponse planet) {
        Map<String, Integer> priorities = SkyXplorePriorityActions.getPriorities(getServerPort(), accessToken1, planet.getPlanetId());
        assertThat(priorities).hasSize(PriorityType.values().length);
        Arrays.stream(PriorityType.values())
            .forEach(priorityType -> assertThat(priorities).containsEntry(priorityType.name().toLowerCase(), 5));
    }

    private static void unknownValue(String accessToken1, PlanetLocationResponse planet) {
        Response unknownValueResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessToken1, planet.getPlanetId(), "asfaed", 4);
        verifyInvalidParam(unknownValueResponse, "priorityType", "unknown value");
    }

    private static void tooLow(String accessToken1, PlanetLocationResponse planet) {
        Response tooLowResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessToken1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 0);
        verifyInvalidParam(tooLowResponse, "priority", "too low");
    }

    private static void tooHigh(String accessToken1, PlanetLocationResponse planet) {
        Response tooHighResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessToken1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 11);
        verifyInvalidParam(tooHighResponse, "priority", "too high");
    }

    private static void update(String accessToken1, PlanetLocationResponse planet) {
        Response updateResponse = SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessToken1, planet.getPlanetId(), PriorityType.CONSTRUCTION, 7);
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);
        Map<String, Integer> modifiedPriorities = SkyXplorePriorityActions.getPriorities(getServerPort(), accessToken1, planet.getPlanetId());
        assertThat(modifiedPriorities).containsEntry(PriorityType.CONSTRUCTION.name().toLowerCase(), 7);

        ApphubWsClient.cleanUpConnections();
    }
}
