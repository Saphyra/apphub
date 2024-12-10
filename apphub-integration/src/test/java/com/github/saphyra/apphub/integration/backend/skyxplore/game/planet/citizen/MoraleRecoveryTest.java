package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.citizen;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenStat;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.UUID;

@Slf4j
public class MoraleRecoveryTest extends BackEndTest {
    public static final int REFERENCE_MORALE = 9000;

    @Test(groups = {"be", "skyxplore"})
    public void checkMoraleRecovery() {
        Integer serverPort = getServerPort();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(serverPort, Constants.DEFAULT_GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId)
            .getPlanetId();

        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(serverPort, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);

        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);
        AwaitilityWrapper.create(60, 5)
            .until(() -> SkyXplorePopulationActions.getPopulation(serverPort, accessTokenId, planetId)
                .stream()
                .anyMatch(citizenResponse -> citizenResponse.getStats().get(CitizenStat.MORALE).getValue() < REFERENCE_MORALE)
            )
            .assertTrue("Morale is not decreased for citizens");

        AwaitilityWrapper.create(180, 10)
            .until(() -> SkyXplorePopulationActions.getPopulation(serverPort, accessTokenId, planetId)
                .stream()
                .allMatch(citizenResponse -> citizenResponse.getStats().get(CitizenStat.MORALE).getValue() >= REFERENCE_MORALE)
            )
            .assertTrue("Morale is not recharged for citizens");
    }
}
