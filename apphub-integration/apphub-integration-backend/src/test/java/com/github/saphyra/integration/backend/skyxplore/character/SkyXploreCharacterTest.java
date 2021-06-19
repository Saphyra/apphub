package com.github.saphyra.integration.backend.skyxplore.character;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
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
        verifyInvalidParam(language, create_nullNameResponse, "name", "must not be null");

        //Create - Character name too short
        SkyXploreCharacterModel create_characterNameTooShortModel = SkyXploreCharacterModel.builder()
            .name("as")
            .build();
        Response create_characterNameTooShortResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooShortModel);
        verifyBadRequest(language, create_characterNameTooShortResponse, ErrorCode.CHARACTER_NAME_TOO_SHORT);

        //Create - Character name too long
        SkyXploreCharacterModel create_characterNameTooLongModel = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        Response create_characterNameTooLongResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooLongModel);
        verifyBadRequest(language, create_characterNameTooLongResponse, ErrorCode.CHARACTER_NAME_TOO_LONG);

        //Create
        SkyXploreCharacterModel createModel = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, createModel);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());

        //Create - Character name already exists
        Response create_characterNameAlreadyExists = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId2, createModel);
        verifyErrorResponse(language, create_characterNameAlreadyExists, 409, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS);

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
}
