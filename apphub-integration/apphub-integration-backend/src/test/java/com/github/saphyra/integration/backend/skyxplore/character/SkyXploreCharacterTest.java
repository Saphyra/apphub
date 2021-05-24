package com.github.saphyra.integration.backend.skyxplore.character;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
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

public class SkyXploreCharacterTest extends BackEndTest {
    @Test(dataProvider = "localeDataProvider")
    public void nullCharacterName(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name(null)
            .build();

        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams()).containsEntry("name", "Must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void characterNameTooShort(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name("as")
            .build();

        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_TOO_SHORT));
    }

    @Test(dataProvider = "localeDataProvider")
    public void characterNameTooLong(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();

        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_TOO_LONG));
    }

    @Test(dataProvider = "localeDataProvider")
    public void characterNameAlreadyExists(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();

        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId2, model);

        assertThat(response.getStatusCode()).isEqualTo(409);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHARACTER_NAME_ALREADY_EXISTS));
    }

    @Test
    public void characterNameAlreadyExistsForTheSameUser() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();

        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void createCharacter() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();

        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        String characterName = SkyXploreCharacterActions.getCharacterName(userData.getEmail());

        assertThat(characterName).isEqualTo(model.getName());
    }

    @Test
    public void editCharacter() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();

        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel editModel = SkyXploreCharacterModel.valid();
        Response response = SkyXploreCharacterActions.getCreateCharacterResponse(language, accessTokenId, editModel);

        assertThat(response.getStatusCode()).isEqualTo(200);

        String newCharacterName = SkyXploreCharacterActions.getCharacterName(userData.getEmail());

        assertThat(newCharacterName).isEqualTo(editModel.getName());
    }
}
