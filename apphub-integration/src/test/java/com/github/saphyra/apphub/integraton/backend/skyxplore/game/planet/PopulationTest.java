package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.CitizenResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkillResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkillType;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class PopulationTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_NAME = "new-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void getAndRenameCitizens(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId1);

        //Get
        List<CitizenResponse> citizens = SkyXplorePopulationActions.getPopulation(language, accessTokenId1, planet.getPlanetId());

        assertThat(citizens.size()).isEqualTo(10);
        citizens.forEach(this::validate);

        CitizenResponse citizen = citizens.get(0);

        //Blank name
        Response blankNameResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), " ");
        verifyInvalidParam(language, blankNameResponse, "value", "must not be null or blank");

        //Too long name
        Response tooLongNameResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        verifyInvalidParam(language, tooLongNameResponse, "value", "too long");

        //Not found
        Response notFoundResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), UUID.randomUUID(), NEW_NAME);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(404);

        //Rename
        Response renameResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, planet.getPlanetId(), citizen.getCitizenId(), NEW_NAME);
        assertThat(renameResponse.getStatusCode()).isEqualTo(200);
        CitizenResponse modifiedCitizen = SkyXplorePopulationActions.getPopulation(language, accessTokenId1, planet.getPlanetId())
            .stream()
            .filter(citizenResponse -> citizenResponse.getCitizenId().equals(citizen.getCitizenId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Citizen not found with id " + citizen.getCitizenId()));
        assertThat(modifiedCitizen.getName()).isEqualTo(NEW_NAME);

        ApphubWsClient.cleanUpConnections();
    }

    private void validate(CitizenResponse citizenResponse) {
        assertThat(citizenResponse.getCitizenId()).isNotNull();
        assertThat(citizenResponse.getName()).isNotNull();
        assertThat(citizenResponse.getMorale()).isEqualTo(1600);
        assertThat(citizenResponse.getSatiety()).isEqualTo(1000);

        assertThat(citizenResponse.getSkills()).hasSize(SkillType.values().length);

        citizenResponse.getSkills()
            .values()
            .forEach(this::validate);
    }

    private void validate(SkillResponse skillResponse) {
        assertThat(skillResponse.getExperience()).isEqualTo(0);
        assertThat(skillResponse.getLevel()).isEqualTo(1);
        assertThat(skillResponse.getNextLevel()).isEqualTo(1000);
    }
}