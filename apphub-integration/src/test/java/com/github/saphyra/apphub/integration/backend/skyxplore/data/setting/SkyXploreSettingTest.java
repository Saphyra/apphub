package com.github.saphyra.apphub.integration.backend.skyxplore.data.setting;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingIdentifier;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSettingTest extends BackEndTest {
    private static final Object DATA_1 = "data-1";
    private static final Object DATA_2 = "data-2";
    private static final UUID LOCATION = UUID.randomUUID();

    @Test(groups = {"be", "skyxplore"})
    public void settingCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel = SkyXploreCharacterModel.valid();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel);
        UUID userId = DynamoDbUtil.getUserIdByEmail(userData.getEmail());

        SkyXploreFlow.startGame(getServerPort(), new Player(accessToken, userId));

        create_nullType(accessToken);
        create_dataTooLarge(accessToken);
        create(accessToken);
        update(accessToken);

        create(accessToken, LOCATION);

        delete_notFound(accessToken);
        delete(accessToken);
    }

    private void delete(String accessToken) {
        assertThat(SkyXploreSettingActions.delete(getServerPort(), accessToken, SettingIdentifier.builder().type(SettingType.POPULATION_ORDER).location(LOCATION).build()))
            .returns(DATA_2, SettingModel::getData);

        assertThat(SkyXploreSettingActions.delete(getServerPort(), accessToken, SettingIdentifier.builder().type(SettingType.POPULATION_ORDER).build())).isNull();
    }

    private void delete_notFound(String accessToken) {
        ResponseValidator.verifyErrorResponse(SkyXploreSettingActions.getDeleteResponse(getServerPort(), accessToken, SettingIdentifier.builder().type(SettingType.POPULATION_HIDE).location(LOCATION).build()), 404, ErrorCode.DATA_NOT_FOUND);
    }

    private void create(String accessToken, UUID location) {
        SettingModel settingModel = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .location(location)
            .data(DATA_1)
            .build();

        SkyXploreSettingActions.createOrUpdate(getServerPort(), accessToken, settingModel);

        assertThat(SkyXploreSettingActions.getSetting(getServerPort(), accessToken, SettingIdentifier.builder().type(SettingType.POPULATION_ORDER).location(location).build()))
            .returns(DATA_1, SettingModel::getData);
    }

    private void update(String accessToken) {
        SettingModel settingModel = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .data(DATA_2)
            .build();

        SkyXploreSettingActions.createOrUpdate(getServerPort(), accessToken, settingModel);

        assertThat(SkyXploreSettingActions.getSetting(getServerPort(), accessToken, SettingIdentifier.builder().type(SettingType.POPULATION_ORDER).build()))
            .returns(DATA_2, SettingModel::getData);
    }

    private void create(String accessToken) {
        SettingModel settingModel = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .data(DATA_1)
            .build();

        SkyXploreSettingActions.createOrUpdate(getServerPort(), accessToken, settingModel);
    }

    private void create_dataTooLarge(String accessToken) {
        SettingModel settingModel = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .data(Stream.generate(() -> "a").limit(1025).collect(Collectors.joining()))
            .build();

        ResponseValidator.verifyInvalidParam(SkyXploreSettingActions.getCreateOrUpdateSettingResponse(getServerPort(), accessToken, settingModel), "data", "too long");
    }

    private void create_nullType(String accessToken) {
        SettingModel settingModel = SettingModel.builder()
            .type(null)
            .data(DATA_1)
            .build();

        ResponseValidator.verifyInvalidParam(SkyXploreSettingActions.getCreateOrUpdateSettingResponse(getServerPort(), accessToken, settingModel), "type", "must not be null");
    }
}
