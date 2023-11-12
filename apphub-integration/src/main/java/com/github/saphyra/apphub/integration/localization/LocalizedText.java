package com.github.saphyra.apphub.integration.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocalizedText {
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
    ;

    private final String text;
}
