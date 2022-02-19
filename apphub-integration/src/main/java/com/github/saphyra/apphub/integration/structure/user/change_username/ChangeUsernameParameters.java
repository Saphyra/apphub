package com.github.saphyra.apphub.integration.structure.user.change_username;

import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangeUsernameParameters {
    private final String username;
    private final String password;

    public static ChangeUsernameParameters valid() {
        return builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.VALID_PASSWORD)
            .build();
    }

    public static ChangeUsernameParameters tooShortUsername() {
        return valid()
            .toBuilder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .build();
    }

    public static ChangeUsernameParameters tooLongUsername() {
        return valid()
            .toBuilder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .build();
    }

    public static ChangeUsernameParameters emptyPassword() {
        return valid()
            .toBuilder()
            .password("")
            .build();
    }
}
