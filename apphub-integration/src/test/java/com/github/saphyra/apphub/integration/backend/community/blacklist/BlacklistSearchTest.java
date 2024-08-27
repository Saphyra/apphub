package com.github.saphyra.apphub.integration.backend.community.blacklist;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistSearchTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void searchUsersToBlacklist() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), testUserData);

        List<SearchResultItem> searchResult = BlacklistActions.search(getServerPort(), accessTokenId, getEmailDomain());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.get(0).getEmail()).isEqualTo(testUserData.getEmail());

        BlacklistActions.createBlacklist(getServerPort(), accessTokenId, searchResult.get(0).getUserId());

        assertThat(BlacklistActions.search(getServerPort(), accessTokenId, getEmailDomain())).isEmpty();
    }
}
