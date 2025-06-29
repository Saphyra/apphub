package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UtilActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRoleTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void isAdminTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        assertThat(UtilActions.isUserAdmin(getServerPort(), accessTokenId)).isFalse();

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        assertThat(UtilActions.isUserAdmin(getServerPort(), accessTokenId)).isTrue();
    }
}
