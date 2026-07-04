package com.github.saphyra.apphub.integration.backend.modules;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulesDataDeletedWithUserTest extends BackEndTest {
    @Test(groups = {"be", "modules"})
    public void modulesDataDeletedWithUser(){
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        ModulesActions.setAsFavorite(getServerPort(), accessToken, "account", true);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> assertThat(DatabaseUtil.getRowCountByValue(userId, "modules", "favorite", "user_id")).isZero());
    }
}
