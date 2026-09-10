package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.RefreshTokenDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <ul>
 *     <li>
 *         DynamoDB:
 *         <ul>
 *             <li>refresh_token</li>
 *             <li>
 *                 user
 *                 <ul>
 *                     <li>profile</li>
 *                     <li>credential</li>
 *                     <li>marked_for_deletion</li>
 *                     <li>role</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         PostgreSQL
 *         <ul>
 *             <li>apphub_user.settings</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class AccountDataDeletedWithUserTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void accountDataDeletedWithUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category("notebook")
            .key("show-archived")
            .value("true")
            .build();
        UserSettingsActions.setUserSetting(getServerPort(), accessToken, setUserSettingsRequest);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(DatabaseUtil.getRowCountByValue(userId, "apphub_user", "settings", "user_id")).isZero();
            assertThat(RefreshTokenDynamoDbRepository.getRefreshTokenCountOfUser(userId)).isZero();
            assertThat(UserDynamoDbRepository.profileExists(userId)).isFalse();
            assertThat(UserDynamoDbRepository.credentialExists(userData.getEmail())).isFalse();
            assertThat(UserDynamoDbRepository.credentialExists(userData.getUsername())).isFalse();
            assertThat(UserDynamoDbRepository.getRolesByUserId(userId)).isEmpty();
            assertThat(UserDynamoDbRepository.markedForDeletionExists(userId)).isFalse();
        });
    }
}
