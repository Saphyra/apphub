package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.citizen;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenStat;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkillResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkillType;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void getAndRenameCitizens(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId1);

        CitizenResponse citizen = getCitizenResponse(language, accessTokenId1, planet);
        blankName(language, accessTokenId1, citizen);
        tooLongName(language, accessTokenId1, citizen);
        notFound(language, accessTokenId1);
        rename(language, accessTokenId1, citizen);
    }

    private CitizenResponse getCitizenResponse(Language language, UUID accessTokenId1, PlanetLocationResponse planet) {
        List<CitizenResponse> citizens = SkyXplorePopulationActions.getPopulation(language, accessTokenId1, planet.getPlanetId());

        assertThat(citizens.size()).isEqualTo(10);
        citizens.forEach(this::validate);

        CitizenResponse citizen = citizens.get(0);
        return citizen;
    }

    private static void blankName(Language language, UUID accessTokenId1, CitizenResponse citizen) {
        Response blankNameResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, citizen.getCitizenId(), " ");
        verifyInvalidParam(language, blankNameResponse, "value", "must not be null or blank");
    }

    private static void tooLongName(Language language, UUID accessTokenId1, CitizenResponse citizen) {
        Response tooLongNameResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, citizen.getCitizenId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        verifyInvalidParam(language, tooLongNameResponse, "value", "too long");
    }

    private static void notFound(Language language, UUID accessTokenId1) {
        Response notFoundResponse = SkyXplorePopulationActions.getRenameCitizenResponse(language, accessTokenId1, UUID.randomUUID(), NEW_NAME);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(404);
    }

    private static void rename(Language language, UUID accessTokenId1, CitizenResponse citizen) {
        CitizenResponse citizenResponse = SkyXplorePopulationActions.renameCitizen(language, accessTokenId1, citizen.getCitizenId(), NEW_NAME);

        assertThat(citizenResponse.getName()).isEqualTo(NEW_NAME);

        ApphubWsClient.cleanUpConnections();
    }

    private void validate(CitizenResponse citizenResponse) {
        assertThat(citizenResponse.getCitizenId()).isNotNull();
        assertThat(citizenResponse.getName()).isNotNull();
        assertThat(citizenResponse.getStats().get(CitizenStat.MORALE).getValue()).isEqualTo(Constants.MAX_CITIZEN_MORALE);
        assertThat(citizenResponse.getStats().get(CitizenStat.SATIETY).getValue()).isEqualTo(Constants.MAX_CITIZEN_SATIETY);

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
