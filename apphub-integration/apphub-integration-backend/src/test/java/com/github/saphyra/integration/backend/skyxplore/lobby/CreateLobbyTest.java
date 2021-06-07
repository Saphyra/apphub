package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyMembersResponse;
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

public class CreateLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void validation(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        //Validation
        verify(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, null), "must not be null");
        verify(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, "aa"), "too short");
        verify(language, SkyXploreLobbyActions.getCreateLobbyResponse(language, accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())), "too long");

        //Create
        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);
        LobbyMembersResponse lobbyMembers = SkyXploreLobbyActions.getLobbyMembers(language, accessTokenId1);
        assertThat(lobbyMembers.getHost()).isEqualTo(LobbyMemberResponse.builder().userId(userId1).characterName(characterModel1.getName()).build());
        assertThat(lobbyMembers.getMembers()).isEmpty();
    }

    private void verify(Language language, Response response, String message) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry("lobbyName", message);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
    }
}
