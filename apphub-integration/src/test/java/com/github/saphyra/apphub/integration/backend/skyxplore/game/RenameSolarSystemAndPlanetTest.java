package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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

public class RenameSolarSystemAndPlanetTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String NEW_SOLAR_SYSTEM_NAME = "new_solar-system-name";
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Test(groups = {"be", "skyxplore"})
    public void renameSolarSystemAndPlanet() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken1, userId1))
            .get(accessToken1);

        MapSolarSystemResponse solarSystemResponse = solarSystem_blank(accessToken1);
        solarSystem_tooLong(accessToken1, solarSystemResponse);
        solarSystem_rename(accessToken1, solarSystemResponse);
        PlanetLocationResponse planetLocationResponse = planet_blank(accessToken1);
        planet_tooLong(accessToken1, planetLocationResponse);
        planet_rename(accessToken1, solarSystemResponse, planetLocationResponse);
    }

    private static MapSolarSystemResponse solarSystem_blank(String accessToken1) {
        MapSolarSystemResponse solarSystemResponse = SkyXploreMapActions.getSolarSystem(getServerPort(), accessToken1);

        Response solarSystem_blankResponse = SkyXploreSolarSystemActions.getRenameSolarSystemResponse(getServerPort(), accessToken1, solarSystemResponse.getSolarSystemId(), " ");

        ResponseValidator.verifyInvalidParam(solarSystem_blankResponse, "newName", "must not be null or blank");
        return solarSystemResponse;
    }

    private static void solarSystem_tooLong(String accessToken1, MapSolarSystemResponse solarSystemResponse) {
        Response solarSystem_tooLongResponse = SkyXploreSolarSystemActions.getRenameSolarSystemResponse(getServerPort(), accessToken1, solarSystemResponse.getSolarSystemId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(solarSystem_tooLongResponse, "newName", "too long");
    }

    private static void solarSystem_rename(String accessToken1, MapSolarSystemResponse solarSystemResponse) {
        SkyXploreSolarSystemActions.renameSolarSystem(getServerPort(), accessToken1, solarSystemResponse.getSolarSystemId(), NEW_SOLAR_SYSTEM_NAME);

        assertThat(SkyXploreMapActions.getSolarSystem(getServerPort(), accessToken1, solarSystemResponse.getSolarSystemId()).getSolarSystemName()).isEqualTo(NEW_SOLAR_SYSTEM_NAME);
    }

    private static PlanetLocationResponse planet_blank(String accessToken1) {
        PlanetLocationResponse planetLocationResponse = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken1);

        Response planet_blankResponse = SkyXplorePlanetActions.getRenamePlanetResponse(getServerPort(), accessToken1, planetLocationResponse.getPlanetId(), " ");

        ResponseValidator.verifyInvalidParam(planet_blankResponse, "newName", "must not be null or blank");
        return planetLocationResponse;
    }

    private static void planet_tooLong(String accessToken1, PlanetLocationResponse planetLocationResponse) {
        Response planet_tooLongResponse = SkyXplorePlanetActions.getRenamePlanetResponse(getServerPort(), accessToken1, planetLocationResponse.getPlanetId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(planet_tooLongResponse, "newName", "too long");
    }

    private static void planet_rename(String accessToken1, MapSolarSystemResponse solarSystemResponse, PlanetLocationResponse planetLocationResponse) {
        SkyXplorePlanetActions.renamePlanet(getServerPort(), accessToken1, planetLocationResponse.getPlanetId(), NEW_PLANET_NAME);

        assertThat(SkyXploreSolarSystemActions.findPlanet(getServerPort(), accessToken1, solarSystemResponse.getSolarSystemId(), planetLocationResponse.getPlanetId()).getPlanetName()).isEqualTo(NEW_PLANET_NAME);

        ApphubWsClient.cleanUpConnections();
    }
}
