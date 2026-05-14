package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerStatus;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void createLobby() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        validation(accessToken1);
        create(characterModel1, accessToken1, userId1);
    }

    private static void validation(String accessToken1) {
        verifyInvalidParam(SkyXploreLobbyActions.getCreateLobbyResponse(getServerPort(), accessToken1, null), "lobbyName", "must not be null");
        verifyInvalidParam(SkyXploreLobbyActions.getCreateLobbyResponse(getServerPort(), accessToken1, "aa"), "lobbyName", "too short");
        verifyInvalidParam(SkyXploreLobbyActions.getCreateLobbyResponse(getServerPort(), accessToken1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())), "lobbyName", "too long");

        assertThat(SkyXploreLobbyActions.isUserInLobby(getServerPort(), accessToken1)).isFalse();
    }

    private static void create(SkyXploreCharacterModel characterModel1, String accessToken1, UUID userId1) {
        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);
        List<LobbyPlayerResponse> lobbyMembers = SkyXploreLobbyActions.getLobbyPlayers(getServerPort(), accessToken1);
        assertThat(lobbyMembers).hasSize(1);
        assertThat(lobbyMembers.get(0).getUserId()).isEqualTo(userId1);
        assertThat(lobbyMembers.get(0).getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(lobbyMembers.get(0).getStatus()).isEqualTo(LobbyPlayerStatus.NOT_READY);

        assertThat(SkyXploreLobbyActions.isUserInLobby(getServerPort(), accessToken1)).isTrue();
    }
}
