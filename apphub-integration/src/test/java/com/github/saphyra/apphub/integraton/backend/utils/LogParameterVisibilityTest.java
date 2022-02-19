package com.github.saphyra.apphub.integraton.backend.utils;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UtilsActions;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.utils.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.integration.structure.utils.SetLogParameterVisibilityRequest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LogParameterVisibilityTest extends BackEndTest {
    private static final String PARAMETER = "parameter";

    @Test
    public void crudTest() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        List<LogParameterVisibilityResponse> createData = UtilsActions.getVisibilities(language, accessTokenId, Arrays.asList(PARAMETER));

        assertThat(createData).hasSize(1);
        assertThat(createData.get(0).getParameter()).isEqualTo(PARAMETER);
        assertThat(createData.get(0).isVisibility()).isTrue();

        UUID id = createData.get(0).getId();

        SetLogParameterVisibilityRequest request = SetLogParameterVisibilityRequest.builder()
            .id(id)
            .visible(false)
            .build();
        UtilsActions.setVisibility(language, accessTokenId, request);

        List<LogParameterVisibilityResponse> queryData = UtilsActions.getVisibilities(language, accessTokenId, Arrays.asList(PARAMETER));

        assertThat(queryData).hasSize(1);
        assertThat(queryData.get(0).getParameter()).isEqualTo(PARAMETER);
        assertThat(queryData.get(0).isVisibility()).isFalse();
    }
}
