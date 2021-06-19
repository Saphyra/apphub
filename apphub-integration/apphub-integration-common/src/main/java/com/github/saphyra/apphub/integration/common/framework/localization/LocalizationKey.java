package com.github.saphyra.apphub.integration.common.framework.localization;

import com.github.saphyra.apphub.integration.common.framework.ErrorCode;

public enum LocalizationKey {
    ALREADY_EXISTS,
    BAD_CREDENTIALS,
    INCORRECT_PASSWORD,
    CATEGORY_NOT_FOUND,
    CHARACTER_NAME_ALREADY_EXISTS,
    CHARACTER_NAME_TOO_SHORT,
    CHARACTER_NAME_TOO_LONG,
    CHAT_ROOM_TITLE_TOO_SHORT,
    CHAT_ROOM_TITLE_TOO_LONG,
    DATA_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    FRIEND_REQUEST_ALREADY_EXISTS,
    FRIEND_REQUEST_NOT_FOUND,
    FRIENDSHIP_ALREADY_EXISTS,
    FRIENDSHIP_NOT_FOUND,
    FORBIDDEN_OPERATION,
    INVALID_PARAM,
    INVALID_TYPE,
    LIST_ITEM_NOT_FOUND,
    LOBBY_MEMBER_NOT_READY,
    LOBBY_NOT_FOUND,
    MISSING_ROLE,
    PASSWORD_TOO_LONG,
    PASSWORD_TOO_SHORT,
    ROLE_ALREADY_EXISTS,
    ROLE_NOT_FOUND,
    SESSION_EXPIRED,
    TOO_FREQUENT_INVITATIONS,
    USER_NOT_FOUND,
    USERNAME_ALREADY_EXISTS,
    USERNAME_TOO_LONG,
    USERNAME_TOO_SHORT,

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
