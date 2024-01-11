package com.github.saphyra.apphub.integration.localization;

import com.github.saphyra.apphub.integration.core.ForRemoval;
import com.github.saphyra.apphub.integration.framework.ErrorCode;

@ForRemoval("skyxplore-react")
public enum LocalizationKey {
    ACCOUNT_LOCKED,
    ALREADY_EXISTS,
    BAD_CREDENTIALS,
    INCORRECT_PASSWORD,
    CATEGORY_NOT_FOUND,
    CHARACTER_NAME_ALREADY_EXISTS,
    DATA_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    FRIEND_REQUEST_ALREADY_EXISTS,
    FRIEND_REQUEST_NOT_FOUND,
    FRIENDSHIP_ALREADY_EXISTS,
    FRIENDSHIP_NOT_FOUND,
    FORBIDDEN_OPERATION,
    GENERAL_ERROR,
    GAME_DELETED,
    GAME_NOT_FOUND,
    INVALID_PARAM,
    INVALID_STATUS,
    INVALID_TYPE,
    LIST_ITEM_NOT_FOUND,
    LOBBY_PLAYER_NOT_READY,
    LOBBY_NOT_FOUND,
    MISSING_ROLE,
    ROLE_ALREADY_EXISTS,
    ROLE_NOT_FOUND,
    SESSION_EXPIRED,
    TOO_FREQUENT_INVITATIONS,
    USER_NOT_FOUND,
    USERNAME_ALREADY_EXISTS,

    NOTIFICATION_LANGUAGE_CHANGED;

    public static LocalizationKey fromErrorCode(ErrorCode errorCode) {
        for (LocalizationKey key : values()) {
            if (errorCode.name().equals(key.name())) {
                return key;
            }
        }

        throw new IllegalArgumentException("No LocalizationKey found for ErrorCode " + errorCode);
    }
}
