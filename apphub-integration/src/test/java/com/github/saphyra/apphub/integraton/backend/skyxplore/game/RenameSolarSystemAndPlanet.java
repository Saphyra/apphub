package com.github.saphyra.apphub.integraton.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RenameSolarSystemAndPlanet extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_SOLAR_SYSTEM_NAME = "new_solar-system-name";
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void renameSolarSystemAndPlanet(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        //SolarSystem - Blank
        MapSolarSystemResponse solarSystemResponse = SkyXploreMapActions.getSolarSystem(language, accessTokenId1);

        Response solarSystem_blankResponse = SkyXploreSolarSystemActions.getRenameSolarSystemResponse(language, accessTokenId1, solarSystemResponse.getSolarSystemId(), " ");

        ResponseValidator.verifyInvalidParam(language, solarSystem_blankResponse, "newName", "must not be null or blank");

        //SolarSystem - Too long
        Response solarSystem_tooLongResponse = SkyXploreSolarSystemActions.getRenameSolarSystemResponse(language, accessTokenId1, solarSystemResponse.getSolarSystemId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(language, solarSystem_tooLongResponse, "newName", "too long");

        //SolarSystem - Rename
        SkyXploreSolarSystemActions.renameSolarSystem(language, accessTokenId1, solarSystemResponse.getSolarSystemId(), NEW_SOLAR_SYSTEM_NAME);

        assertThat(SkyXploreMapActions.getSolarSystem(language, accessTokenId1, solarSystemResponse.getSolarSystemId()).getSolarSystemName()).isEqualTo(NEW_SOLAR_SYSTEM_NAME);

        //Planet - Blank
        PlanetLocationResponse planetLocationResponse = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId1);

        Response planet_blankResponse = SkyXplorePlanetActions.getRenamePlanetResponse(language, accessTokenId1, planetLocationResponse.getPlanetId(), " ");

        ResponseValidator.verifyInvalidParam(language, planet_blankResponse, "newName", "must not be null or blank");

        //Planet - Too long
        Response planet_tooLongResponse = SkyXplorePlanetActions.getRenamePlanetResponse(language, accessTokenId1, planetLocationResponse.getPlanetId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(language, planet_tooLongResponse, "newName", "too long");

        //Planet - Rename
        SkyXplorePlanetActions.renamePlanet(language, accessTokenId1, planetLocationResponse.getPlanetId(), NEW_PLANET_NAME);

        assertThat(SkyXploreSolarSystemActions.findPlanet(language, accessTokenId1, solarSystemResponse.getSolarSystemId(), planetLocationResponse.getPlanetId()).getPlanetName()).isEqualTo(NEW_PLANET_NAME);

        ApphubWsClient.cleanUpConnections();
    }
}
