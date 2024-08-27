package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.Range;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameSettingsTest extends BackEndTest {
    private static final SkyXploreGameSettings VALID_SETTINGS = SkyXploreGameSettings.builder()
        .maxPlayersPerSolarSystem(5)
        .additionalSolarSystems(new Range<>(5, 10))
        .planetsPerSolarSystem(new Range<>(3, 7))
        .planetSize(new Range<>(13, 17))
        .build();

    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void gameSettings() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, characterModel1);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessTokenId1, GAME_NAME);

        SkyXploreGameSettings settings = SkyXploreLobbyActions.getGameSettings(getServerPort(), accessTokenId1);

        assertThat(settings.getMaxPlayersPerSolarSystem()).isEqualTo(2);
        assertThat(settings.getAdditionalSolarSystems()).isEqualTo(new Range<>(1, 2));
        assertThat(settings.getPlanetsPerSolarSystem()).isEqualTo(new Range<>(0, 3));
        assertThat(settings.getPlanetSize()).isEqualTo(new Range<>(10, 15));

        validation(VALID_SETTINGS.toBuilder().maxPlayersPerSolarSystem(null), accessTokenId1, "maxPlayersPerSolarSystem", "must not be null");
        validation(VALID_SETTINGS.toBuilder().maxPlayersPerSolarSystem(0), accessTokenId1, "maxPlayersPerSolarSystem", "too low");

        validation(VALID_SETTINGS.toBuilder().additionalSolarSystems(null), accessTokenId1, "additionalSolarSystems", "must not be null");
        validation(VALID_SETTINGS.toBuilder().additionalSolarSystems(new Range<>(-1, 5)), accessTokenId1, "additionalSolarSystems.min", "too low");
        validation(VALID_SETTINGS.toBuilder().additionalSolarSystems(new Range<>(0, 31)), accessTokenId1, "additionalSolarSystems.max", "too high");
        validation(VALID_SETTINGS.toBuilder().additionalSolarSystems(new Range<>(5, 4)), accessTokenId1, "additionalSolarSystems.max", "too low");

        validation(VALID_SETTINGS.toBuilder().planetsPerSolarSystem(null), accessTokenId1, "planetsPerSolarSystem", "must not be null");
        validation(VALID_SETTINGS.toBuilder().planetsPerSolarSystem(new Range<>(-1, 7)), accessTokenId1, "planetsPerSolarSystem.min", "too low");
        validation(VALID_SETTINGS.toBuilder().planetsPerSolarSystem(new Range<>(3, 11)), accessTokenId1, "planetsPerSolarSystem.max", "too high");
        validation(VALID_SETTINGS.toBuilder().planetsPerSolarSystem(new Range<>(3, 2)), accessTokenId1, "planetsPerSolarSystem.max", "too low");

        validation(VALID_SETTINGS.toBuilder().planetSize(null), accessTokenId1, "planetSize", "must not be null");
        validation(VALID_SETTINGS.toBuilder().planetSize(new Range<>(9, 17)), accessTokenId1, "planetSize.min", "too low");
        validation(VALID_SETTINGS.toBuilder().planetSize(new Range<>(13, 21)), accessTokenId1, "planetSize.max", "too high");
        validation(VALID_SETTINGS.toBuilder().planetSize(new Range<>(13, 12)), accessTokenId1, "planetSize.max", "too low");

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessTokenId1, accessTokenId1);
        Response response = SkyXploreLobbyActions.getEditSettingsResponse(getServerPort(), accessTokenId1, VALID_SETTINGS);

        assertThat(response.getStatusCode()).isEqualTo(200);

        settings = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_SETTINGS_MODIFIED)
            .orElseThrow()
            .getPayloadAs(SkyXploreGameSettings.class);

        assertThat(settings).isEqualTo(VALID_SETTINGS);
    }

    private void validation(SkyXploreGameSettings.SkyXploreGameSettingsBuilder builder, UUID accessTokenId, String field, String value) {
        Response response = SkyXploreLobbyActions.getEditSettingsResponse(getServerPort(), accessTokenId, builder.build());

        ResponseValidator.verifyInvalidParam(response, field, value);
    }
}
