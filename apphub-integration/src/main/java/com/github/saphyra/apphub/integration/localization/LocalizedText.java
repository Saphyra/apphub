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

    //INDEX/ACCOUNT
    INDEX_SUCCESSFULLY_LOGGED_OUT("Successfully logged out."),
    INDEX_BAD_CREDENTIALS("Unknown combination of e-mail address and password."),
    INDEX_ACCOUNT_LOCKED("Account locked. Try again later."),
    INDEX_EMPTY_CREDENTIALS("Please fill e-mail address and password!"),
    INDEX_USERNAME_ALREADY_IN_USE("Username already in use."),
    INDEX_EMAIL_ALREADY_IN_USE("E-mail address is already in use."),

    //NOTEBOOK
    NOTEBOOK_TITLE_MUST_NOT_BE_BLANK("Title must not be blank."),
    NOTEBOOK_URL_MUST_NOT_BE_BLANK("URL must not be blank."),
    NOTEBOOK_COLUMN_NAME_MUST_NOT_BE_BLANK("Name of the column must not be blank."),

    //SKYXPLORE
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
    NOTEBOOK_LINK_LABEL_BLANK("Link label must not be blank."),
    ;

    private final String text;
}
