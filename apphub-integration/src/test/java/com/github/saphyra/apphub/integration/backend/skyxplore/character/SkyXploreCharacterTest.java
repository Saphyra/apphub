package com.github.saphyra.apphub.integration.backend.skyxplore.character;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void createAndEditCharacter(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        create_nullName(language, accessTokenId1);
        create_characterNameTooShort(language, accessTokenId1);
        create_characterNameTooLong(language, accessTokenId1);
        SkyXploreCharacterModel createModel = getCreateModel(language, userData1, accessTokenId1);
        edit_characterNameAlreadyExists(language, accessTokenId2, createModel);
        edit_noChange(language, userData1, accessTokenId1, createModel);
        edit(language, userData1, accessTokenId1);
    }

    private static void create_nullName(Language language, UUID accessTokenId1) {
        SkyXploreCharacterModel create_nullNameModel = SkyXploreCharacterModel.builder()
            .name(null)
            .build();
        Response create_nullNameResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_nullNameModel);
        verifyInvalidParam(language, create_nullNameResponse, "name", "must not be null");
    }

    private static void create_characterNameTooShort(Language language, UUID accessTokenId1) {
        SkyXploreCharacterModel create_characterNameTooShortModel = SkyXploreCharacterModel.builder()
            .name("as")
            .build();
        Response create_characterNameTooShortResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooShortModel);
        verifyInvalidParam(language, create_characterNameTooShortResponse, "characterName", "too short");
    }

    private static void create_characterNameTooLong(Language language, UUID accessTokenId1) {
        SkyXploreCharacterModel create_characterNameTooLongModel = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        Response create_characterNameTooLongResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, create_characterNameTooLongModel);
        verifyInvalidParam(language, create_characterNameTooLongResponse, "characterName", "too long");
    }

    private static SkyXploreCharacterModel getCreateModel(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        SkyXploreCharacterModel createModel = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, createModel);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());
        return createModel;
    }

    private static void edit_characterNameAlreadyExists(Language language, UUID accessTokenId2, SkyXploreCharacterModel createModel) {
        Response create_characterNameAlreadyExists = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId2, createModel);
        verifyErrorResponse(language, create_characterNameAlreadyExists, 409, ErrorCode.CHARACTER_NAME_ALREADY_EXISTS);
    }

    private static void edit_noChange(Language language, RegistrationParameters userData1, UUID accessTokenId1, SkyXploreCharacterModel createModel) {
        Response edit_noChangeResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, createModel);
        assertThat(edit_noChangeResponse.getStatusCode()).isEqualTo(200);
        assertThat(SkyXploreCharacterActions.getCharacterName(userData1.getEmail())).isEqualTo(createModel.getName());
    }

    private static void edit(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        SkyXploreCharacterModel editModel = SkyXploreCharacterModel.valid();
        Response editResponse = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId1, editModel);
        assertThat(editResponse.getStatusCode()).isEqualTo(200);
        String newCharacterName = SkyXploreCharacterActions.getCharacterName(userData1.getEmail());
        assertThat(newCharacterName).isEqualTo(editModel.getName());
    }
}
