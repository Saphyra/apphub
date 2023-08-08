package com.github.saphyra.apphub.integraton.backend.community.blacklist;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistSearchTest extends BackEndTest {
    @Test(groups = "community")
    public void searchUsersToBlacklist() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(language, testUserData);

        List<SearchResultItem> searchResult = BlacklistActions.search(language, accessTokenId, getEmailDomain());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.get(0).getEmail()).isEqualTo(testUserData.getEmail());

        BlacklistActions.createBlacklist(language, accessTokenId, searchResult.get(0).getUserId());

        assertThat(BlacklistActions.search(language, accessTokenId, getEmailDomain())).isEmpty();
    }
}
