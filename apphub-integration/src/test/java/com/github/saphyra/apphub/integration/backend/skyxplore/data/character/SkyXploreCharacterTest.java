package com.github.saphyra.apphub.integration.backend.skyxplore.data.character;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
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
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        create_nullName(accessToken1);
        create_characterNameTooShort(accessToken1);
        create_characterNameTooLong(accessToken1);
        SkyXploreCharacterModel createModel = getCreateModel(accessToken1, userId1);
        edit_characterNameAlreadyExists(accessToken2, createModel);
        edit_noChange(accessToken1, createModel, userId1);
        edit(accessToken1, userId1);
    }

    private static void create_nullName(String accessToken1) {
        SkyXploreCharacterModel create_nullNameModel = SkyXploreCharacterModel.builder()
            .name(null)
            .build();
        Response create_nullNameResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken1, create_nullNameModel);
        verifyInvalidParam(create_nullNameResponse, "name", "must not be null");
    }

    private static void create_characterNameTooShort(String accessToken1) {
        SkyXploreCharacterModel create_characterNameTooShortModel = SkyXploreCharacterModel.builder()
            .name("as")
            .build();
        Response create_characterNameTooShortResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken1, create_characterNameTooShortModel);
        verifyInvalidParam(create_characterNameTooShortResponse, "characterName", "too short");
    }

    private static void create_characterNameTooLong(String accessToken1) {
        SkyXploreCharacterModel create_characterNameTooLongModel = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        Response create_characterNameTooLongResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken1, create_characterNameTooLongModel);
        verifyInvalidParam(create_characterNameTooLongResponse, "characterName", "too long");
    }

    private static SkyXploreCharacterModel getCreateModel(String accessToken1, UUID userId1) {
        SkyXploreCharacterModel createModel = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, createModel);
        assertThat(SkyXploreCharacterActions.getCharacterName(userId1)).isEqualTo(createModel.getName());
        return createModel;
    }

    private static void edit_characterNameAlreadyExists(String accessToken2, SkyXploreCharacterModel createModel) {
        Response create_characterNameAlreadyExists = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken2, createModel);
        verifyErrorResponse(create_characterNameAlreadyExists, 409, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS);
    }

    private static void edit_noChange(String accessToken1, SkyXploreCharacterModel createModel, UUID userId1) {
        Response edit_noChangeResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken1, createModel);
        assertThat(edit_noChangeResponse.getStatusCode()).isEqualTo(200);
        assertThat(SkyXploreCharacterActions.getCharacterName(userId1)).isEqualTo(createModel.getName());
    }

    private static void edit(String accessToken1, UUID userId1) {
        SkyXploreCharacterModel editModel = SkyXploreCharacterModel.valid();
        Response editResponse = SkyXploreCharacterActions.getCreateCharacterResponse(getServerPort(), accessToken1, editModel);
        assertThat(editResponse.getStatusCode()).isEqualTo(200);
        String newCharacterName = SkyXploreCharacterActions.getCharacterName(userId1);
        assertThat(newCharacterName).isEqualTo(editModel.getName());
    }
}
