package com.github.saphyra.apphub.integration.backend.skyxplore.data.character;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreCharacterTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void createAndEditCharacter() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        create_nullName(accessTokenId1);
        create_characterNameTooShort(accessTokenId1);
        create_characterNameTooLong(accessTokenId1);
        SkyXploreCharacterModel createModel = getCreateModel(userData1, accessTokenId1);
        edit_characterNameAlreadyExists(accessTokenId2, createModel);
        edit_noChange(userData1, accessTokenId1, createModel);
        edit(userData1, accessTokenId1);
    }

    private static void create_nullName(UUID accessTokenId1) {
        SkyXploreCharacterModel create_nullNameModel = SkyXploreCharacterModel.builder()
            .name(null)
            .build();
        Response create_nullNameResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId1, create_nullNameModel);
        verifyInvalidParam(create_nullNameResponse, "name", "must not be null");
    }

    private static void create_characterNameTooShort(UUID accessTokenId1) {
        SkyXploreCharacterModel create_characterNameTooShortModel = SkyXploreCharacterModel.builder()
            .name("as")
            .build();
        Response create_characterNameTooShortResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId1, create_characterNameTooShortModel);
        verifyInvalidParam(create_characterNameTooShortResponse, "characterName", "too short");
    }

    private static void create_characterNameTooLong(UUID accessTokenId1) {
        SkyXploreCharacterModel create_characterNameTooLongModel = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        Response create_characterNameTooLongResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId1, create_characterNameTooLongModel);
        verifyInvalidParam(create_characterNameTooLongResponse, "characterName", "too long");
    }

    private static SkyXploreCharacterModel getCreateModel(RegistrationParameters userData1, UUID accessTokenId1) {
        SkyXploreCharacterModel createModel = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, createModel);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());
        return createModel;
    }

    private static void edit_characterNameAlreadyExists(UUID accessTokenId2, SkyXploreCharacterModel createModel) {
        Response create_characterNameAlreadyExists = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId2, createModel);
        verifyErrorResponse(create_characterNameAlreadyExists, 409, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS);
    }

    private static void edit_noChange(RegistrationParameters userData1, UUID accessTokenId1, SkyXploreCharacterModel createModel) {
        Response edit_noChangeResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId1, createModel);
        assertThat(edit_noChangeResponse.getStatusCode()).isEqualTo(200);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());
    }

    private static void edit(RegistrationParameters userData1, UUID accessTokenId1) {
        SkyXploreCharacterModel editModel = SkyXploreCharacterModel.valid();
        Response editResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessTokenId1, editModel);
        assertThat(editResponse.getStatusCode()).isEqualTo(200);
        String newCharacterName = SkyXploreCharacterActions.getCharacterName(userData1.getEmail());
        assertThat(newCharacterName).isEqualTo(editModel.getName());
    }
}
