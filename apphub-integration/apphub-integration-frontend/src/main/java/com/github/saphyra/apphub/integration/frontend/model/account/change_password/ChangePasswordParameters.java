package com.github.saphyra.apphub.integration.frontend.model.account.change_password;

import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ChangePasswordParameters {
    private final String newPassword;
    private final String confirmPassword;
    private final String password;

    public static ChangePasswordParameters valid() {
        return builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .confirmPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .build();
    }

    public static ChangePasswordParameters tooShortPassword() {
        return valid()
            .toBuilder()
            .newPassword(DataConstants.TOO_SHORT_PASSWORD)
            .confirmPassword(DataConstants.TOO_SHORT_PASSWORD)
            .build();
    }

    public static ChangePasswordParameters tooLongPassword() {
        return valid()
            .toBuilder()
            .newPassword(DataConstants.TOO_LONG_PASSWORD)
            .confirmPassword(DataConstants.TOO_LONG_PASSWORD)
            .build();
    }

    public static ChangePasswordParameters incorrectConfirmPassword() {
        return valid()
            .toBuilder()
            .confirmPassword(DataConstants.INCORRECT_PASSWORD)
            .build();
    }

    public static ChangePasswordParameters emptyPassword() {
        return valid()
            .toBuilder()
            .password("")
            .build();
    }
}
