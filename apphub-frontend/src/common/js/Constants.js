const Constants = {
    //REQUEST
    COOKIE_LOCALE: "language",
    DEFAULT_LOCALE: "hu",
    HEADER_BROWSER_LANGUAGE: "BrowserLanguage",
    HEADER_REQUEST_TYPE_NAME: "Request-Type",
    HEADER_REQUEST_TYPE_VALUE: "rest",

    //VALIDATION
    MIN_USERNAME_LENGTH: 3,
    MAX_USERNAME_LENGTH: 30,
    MIN_PASSWORD_LENGTH: 6,
    MAX_PASSWORD_LENGTH: 30,
    MIN_CHARACTER_NAME_LENGTH: 3,
    MAX_CHARACTER_NAME_LENGTH: 30,
    MIN_GAME_NAME_LENGTH: 3,
    MAX_GAME_NAME_LENGTH: 30,
    
    //PAGES
    MODULES_PAGE: "/web/modules",
    INDEX_PAGE: "/web",
    SKYXPLORE_MAIN_MENU_PAGE: "/web/skyxplore",
    SKYXPLORE_CHARACTER_PAGE: "/web/skyxplore/character",
    SKYXPLORE_LOBBY_PAGE: "/web/skyxplore/lobby",
    SKYXPLORE_GAME_PAGE: "/web/skyxplore/game",

    //SkyXplore Lobby Settings limits
    MIN_PLAYERS_PER_SOLAR_SYSTEM: 1,
    MAX_PLAYERS_PER_SOLAR_SYSTEM: 5,
    ADDITIONAL_SOLAR_SYSTEMS_MIN: 0,
    ADDITIONAL_SOLAR_SYSTEMS_MAX: 30,
    PLANETS_PER_SOLAR_SYSTEM_MIN: 0,
    PLANETS_PER_SOLAR_SYSTEM_MAX: 10,
    PLANET_SIZE_MIN: 10,
    PLANET_SIZE_MAX: 20,

    SKYXPLORE_LOBBY_TYPE_NEW: "NEW_GAME",
    SKYXPLORE_LOBBY_TYPE_LOAD: "LOAD_GAME",
}

export default Constants;