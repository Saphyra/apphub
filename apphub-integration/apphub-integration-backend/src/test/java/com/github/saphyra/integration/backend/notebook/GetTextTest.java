package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.TextResponse;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetTextTest extends BackEndTest {
    private static final String CONTENT = "content";
    private static final String TITLE = "title";

    @Test
    public void getText() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest request = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .build();
        UUID textId = NotebookActions.createText(language, accessTokenId, request);

        TextResponse textResponse = NotebookActions.getText(language, accessTokenId, textId);
        assertThat(textResponse.getTextId()).isEqualTo(textId);
        assertThat(textResponse.getTitle()).isEqualTo(TITLE);
        assertThat(textResponse.getContent()).isEqualTo(CONTENT);
    }
}
