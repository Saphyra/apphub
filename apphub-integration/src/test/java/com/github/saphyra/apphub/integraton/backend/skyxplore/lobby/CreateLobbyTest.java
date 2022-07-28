package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyMemberStatus;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyMembersResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void createLobby(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        //Validation
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, null), "lobbyName", "must not be null");
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, "aa"), "lobbyName", "too short");
        verifyInvalidParam(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())), "lobbyName", "too long");

        //Create
        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);
        LobbyMembersResponse lobbyMembers = SkyXploreLobbyActions.getLobbyMembers(language, accessTokenId1);
        assertThat(lobbyMembers.getHost()).isEqualTo(LobbyMemberResponse.builder().userId(userId1).characterName(characterModel1.getName()).status(LobbyMemberStatus.NOT_READY).build());
        assertThat(lobbyMembers.getMembers()).isEmpty();
    }
}
