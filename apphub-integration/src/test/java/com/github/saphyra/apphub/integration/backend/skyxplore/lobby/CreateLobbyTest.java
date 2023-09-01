package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberStatus;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void createLobby(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        validation(language, accessTokenId1);
        create(language, characterModel1, accessTokenId1, userId1);
    }

    private static void validation(Language language, UUID accessTokenId1) {
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, null), "lobbyName", "must not be null");
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, "aa"), "lobbyName", "too short");
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())), "lobbyName", "too long");

        assertThat(SkyXploreLobbyActions.isUserInLobby(language, accessTokenId1)).isFalse();
    }

    private static void create(Language language, SkyXploreCharacterModel characterModel1, UUID accessTokenId1, UUID userId1) {
        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);
        List<LobbyMemberResponse> lobbyMembers = SkyXploreLobbyActions.getLobbyMembers(language, accessTokenId1);
        assertThat(lobbyMembers).hasSize(1);
        assertThat(lobbyMembers.get(0).getUserId()).isEqualTo(userId1);
        assertThat(lobbyMembers.get(0).getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(lobbyMembers.get(0).getStatus()).isEqualTo(LobbyMemberStatus.NOT_READY);

        assertThat(SkyXploreLobbyActions.isUserInLobby(language, accessTokenId1)).isTrue();
    }
}
