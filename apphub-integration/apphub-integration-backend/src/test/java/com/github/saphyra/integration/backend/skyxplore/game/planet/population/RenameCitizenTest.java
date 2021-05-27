package com.github.saphyra.integration.backend.skyxplore.game.planet.population;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.CitizenResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RenameCitizenTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_NAME = "new-name";

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void blankNewName(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        CitizenResponse citizen = getRandomCitizen(language, accessTokenId1, planet.getPlanetId());

        Response response = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), " ");

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry("value", "must not be blank");
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void newNameTooLong(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        CitizenResponse citizen = getRandomCitizen(language, accessTokenId1, planet.getPlanetId());

        Response response = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry("value", "too long");
    }

    @Test(groups = "skyxplore")
    public void citizenNotFound() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        Response response = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), UUID.randomUUID(), NEW_NAME);

        assertThat(response.getStatusCode()).isEqualTo(404);
    }

    @Test(groups = "skyxplore")
    public void renameCitizen() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        CitizenResponse citizen = getRandomCitizen(language, accessTokenId1, planet.getPlanetId());

        Response response = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), NEW_NAME);

        assertThat(response.getStatusCode()).isEqualTo(200);

        CitizenResponse modifiedCitizen = SkyXplorePopulationActions.getPopulation(language, accessTokenId1, planet.getPlanetId())
            .stream()
            .filter(citizenResponse -> citizenResponse.getCitizenId().equals(citizen.getCitizenId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Citizen not found with id " + citizen.getCitizenId()));

        assertThat(modifiedCitizen.getName()).isEqualTo(NEW_NAME);
    }

    private CitizenResponse getRandomCitizen(Language language, UUID accessTokenId1, UUID planetId) {
        return SkyXplorePopulationActions.getPopulation(language, accessTokenId1, planetId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No citizen found"));
    }
}
