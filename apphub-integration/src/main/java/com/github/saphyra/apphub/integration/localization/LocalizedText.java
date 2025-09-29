package com.github.saphyra.apphub.integration.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocalizedText {
    //PLATFORM
    ERROR_MISSING_ROLE("You are not allowed to perform this operation."),
    NOTIFICATION_LANGUAGE_CHANGED_EN("Language changed."),
    NOTIFICATION_LANGUAGE_CHANGED_HU("Nyelv megváltoztatva."),
    INCORRECT_PASSWORD("Incorrect password."),
    ACCOUNT_LOCKED("Account locked. Try again later."),
    EMPTY_PASSWORD("Password must not be empty."),

    //INDEX/ACCOUNT
    INDEX_SUCCESSFULLY_LOGGED_OUT("Successfully logged out."),
    INDEX_BAD_CREDENTIALS("Unknown combination of e-mail address and password."),
    INDEX_EMPTY_CREDENTIALS("Please fill e-mail address and password!"),
    INDEX_USERNAME_ALREADY_IN_USE("Username already in use."),
    INDEX_EMAIL_ALREADY_IN_USE("E-mail address is already in use."),
    ACCOUNT_EMAIL_CHANGED("Email changed."),
    ACCOUNT_USERNAME_CHANGED("Username changed."),
    ACCOUNT_PASSWORD_CHANGED("Password changed."),
    ACCOUNT_DELETED("Account deleted."),

    //NOTEBOOK
    NOTEBOOK_TITLE_MUST_NOT_BE_BLANK("Title must not be blank."),
    NOTEBOOK_URL_MUST_NOT_BE_BLANK("URL must not be blank."),
    NOTEBOOK_COLUMN_NAME_MUST_NOT_BE_BLANK("Name of the column must not be blank."),
    NOTEBOOK_LINK_LABEL_BLANK("Link label must not be blank."),
    NOTEBOOK_PIN_GROUP_NAME_BLANK("Pin group name must not be blank."),
    NOTEBOOK_PIN_GROUP_NAME_TOO_LONG("Pin group name too long. (Maximum 30 characters)"),

    //SKYXPLORE
    SKYXPLORE_CHAT_MESSAGE_TOO_LONG("Message too long."),
    SKYXPLORE_CHARACTER_SAVED("Character saved."),
    SKYXPLORE_CHARACTER_NAME_ALREADY_EXISTS("Character name already exists."),
    SKYXPLORE_LOBBY_PLAYERS_NOT_READY("Not all the players are ready."),
    SKYXPLORE_LOBBY_INVITATION_SENT_RECENTLY("You have invited this player recently. Please wait a few seconds before trying again."),
    SKYXPLORE_LOBBY_ONLY_ONE_ALLIANCE("Every player is in the same alliance. Game is boring with no one to conquer."),

    //SKYXPLORE GAME
    SKYXPLORE_GAME_SOLAR_SYSTEM_NAME_BLANK("Solar System name must not be blank."),
    SKYXPLORE_GAME_SOLAR_SYSTEM_NAME_TOO_LONG("Solar System name too long. (Maximum 30 characters.)"),
    SKYXPLORE_GAME_PLANET_NAME_BLANK("Planet name must not be blank."),
    SKYXPLORE_GAME_PLANET_NAME_TOO_LONG("Planet name too long. (Maximum 30 characters.)"),
    SKYXPLORE_GAME_STORAGE_USED("Storage still in use. Free up some space before you deconstruct it."),
    SKYXPLORE_GAME_STORAGE_SETTING_SAVED("Storage setting saved."),

    //ADMIN PANEL
    //ROLES_FOR_ALL
    ROLES_FOR_ALL_ROLE_REVOKED("Role successfully revoked from all users."),
    ROLES_FOR_ALL_ROLE_ADDED("Role successfully granted to all users."),
    ROLE_MANAGEMENT_ROLE_GRANTED("Role granted."),
    ROLE_MANAGEMENT_ROLE_REVOKED("Role revoked."),

    //DISABLED_ROLE_MANAGEMENT
    DISABLED_ROLE_MANAGEMENT_ROLE_DISABLED("Role disabled."),
    DISABLED_ROLE_MANAGEMENT_ROLE_ENABLED("Role enabled."),

    //BAN
    BAN_SELECT_ROLE("Select role."),
    BAN_DURATION_TOO_SHORT("Duration too short."),
    BAN_SELECT_TIME_UNIT("Select time unit."),
    BAN_BLANK_REASON("Please provide why the user is banned."),
    BAN_EMPTY_DATE("Please select when the user should be deleted."),

    //VILLANY ATESZ
    VILLANY_ATESZ_CONTACTS_BLANK_NAME("Name must not be blank."),
    VILLANY_ATESZ_STOCK_CATEGORIES_BLANK_NAME("Name must not be blank."),
    VILLANY_ATESZ_STOCK_NEW_ITEM_CHOOSE_CATEGORY("Choose category!"),
    VILLANY_ATESZ_STOCK_NEW_ITEM_BLANK_NAME("Name must not be blank."),
    VILLANY_ATESZ_STOCK_NEW_ITEM_CREATED("Item created."),
    VILLANY_ATESZ_STOCK_ACQUISITION_CHOOSE_CATEGORY("Select category for all of the entries!"),
    VILLANY_ATESZ_STOCK_ACQUISITION_CHOOSE_STOCK_ITEM("Specify the item for all of the entries!"),
    VILLANY_ATESZ_STOCK_ACQUISITION_ITEMS_STORED("Items stored."),
    VILLANY_ATESZ_STOCK_ZERO_AMOUNT("Amount must not be zero."),
    VILLANY_ATESZ_TOOLBOX_NEW_NAME_MUST_NOT_BE_BLANK("Name must not be blank."),
    VILLANY_ATESZ_TOOLBOX_NEW_CREATED("Tool created."),

    //CALENDAR
    CALENDAR_EMPTY_START_DATE("Start date is empty."),
    CALENDAR_EMPTY_END_DATE("End date is empty."),
    CALENDAR_END_DATE_BEFORE_START_DATE("End date is before start date."),
    CALENDAR_BLANK_TITLE("Title is blank."),
    CALENDAR_REPEAT_FOR_DAYS_TOO_LOW("Repetition is too low."),
    CALENDAR_EVERY_X_DAYS_REPETITION_DATA_TOO_LOW("Repetition is too low."),
    CALENDAR_NO_DAYS_DEFINED("No day defined for repetition."),
    CALENDAR_LABEL_ALREADY_EXISTS("Label already exists."),
    CALENDAR_LABEL_TOO_SHORT("Label too short."),
    CALENDAR_LABEL_TOO_LONG("Label too long. (Maximum 255 characters)"),
    CALENDAR_EVENT_CREATED("Event created."),
    ;

    private final String text;
}
