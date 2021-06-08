package com.github.saphyra.integration.backend.skyxplore.character;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
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

public class SkyXploreCharacterTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void createAndEditCharacter(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        //Create - Null name
        SkyXploreCharacterModel create_nullNameModel = SkyXploreCharacterModel.builder()
            .name(null)
            .build();
        Response create_nullNameResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_nullNameModel);
        verifyInvalidParam(language, create_nullNameResponse);

        //Create - Character name too short
        SkyXploreCharacterModel create_characterNameTooShortModel = SkyXploreCharacterModel.builder()
            .name("as")
            .build();
        Response create_characterNameTooShortResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooShortModel);
        verifyCharacterNameTooShort(language, create_characterNameTooShortResponse);

        //Create - Character name too long
        SkyXploreCharacterModel create_characterNameTooLongModel = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        Response create_characterNameTooLongResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooLongModel);
        verifyCharacterNameTooLong(language, create_characterNameTooLongResponse);

        //Create
        SkyXploreCharacterModel createModel = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, createModel);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());

        //Create - Character name already exists
        Response create_characterNameAlreadyExists = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId2, createModel);
        verifyCharacterNameAlreadyExists(language, create_characterNameAlreadyExists);

        //Edit - No change
        Response edit_noChangeResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, createModel);
        assertThat(edit_noChangeResponse.getStatusCode()).isEqualTo(200);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());

        //Edit
        SkyXploreCharacterModel editModel = SkyXploreCharacterModel.valid();
        Response editResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, editModel);
        assertThat(editResponse.getStatusCode()).isEqualTo(200);
        String newCharacterName = SkyXploreCharacterActions.getCharacterName(userData1.getEmail());
        assertThat(newCharacterName).isEqualTo(editModel.getName());
    }

    private void verifyCharacterNameAlreadyExists(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(409);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_ALREADY_EXISTS));
    }

    private void verifyCharacterNameTooLong(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_TOO_LONG));
    }

    private void verifyCharacterNameTooShort(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_TOO_SHORT));
    }

    private void verifyInvalidParam(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry("name", "Must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
    }
}
