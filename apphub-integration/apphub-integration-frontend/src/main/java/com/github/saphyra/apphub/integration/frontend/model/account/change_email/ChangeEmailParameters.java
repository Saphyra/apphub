package com.github.saphyra.apphub.integration.frontend.model.account.change_email;

import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.RandomDataProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangeEmailParameters {
    private final String email;
    private final String password;

    public static ChangeEmailParameters valid() {
        return builder()
            .email(RandomDataProvider.generateEmail())
            .password(DataConstants.VALID_PASSWORD)
            .build();
    }

    public static ChangeEmailParameters invalidEmail() {
        return valid()
            .toBuilder()
            .email(DataConstants.INVALID_EMAIL)
            .build();
    }

    public static ChangeEmailParameters emptyPassword() {
        return valid()
            .toBuilder()
            .password("")
            .build();
    }
}
