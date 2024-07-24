const KILOBYTES = 1024;
const MEGABYTES = KILOBYTES * 1024;
const GIGABYTES = MEGABYTES * 1024;

const Constants = {
    //REQUEST
    COOKIE_LOCALE: "language",
    DEFAULT_LOCALE: "en",
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
    FILE_SIZE_LIMIT: 104_857_600, //100 MB
    MAX_PIN_GROUP_NAME_LENGTH: 30,
    SKYXPLORE_MAX_CHAT_MESSAGE_SIZE: 1024,

    //PAGES
    MODULES_PAGE: "/web/modules",
    ERROR_REPORT_PAGE: "/web/admin-panel/error-report",
    INDEX_PAGE: "/web",
    SKYXPLORE_MAIN_MENU_PAGE: "/web/skyxplore",
    SKYXPLORE_CHARACTER_PAGE: "/web/skyxplore/character",
    SKYXPLORE_LOBBY_PAGE: "/web/skyxplore/lobby",
    SKYXPLORE_GAME_PAGE: "/web/skyxplore/game",
    NOTEBOOK_PAGE: "/web/notebook",
    NOTEBOOK_NEW_PAGE: "/web/notebook/new",
    NOTEBOOK_EDIT_PAGE: "/web/notebook/edit",
    VILLANY_ATESZ_INDEX_PAGE: "/web/villany-atesz",
    VILLANY_ATESZ_CONTACTS_PAGE: "/web/villany-atesz/contacts",
    VILLANY_ATESZ_STOCK_PAGE: "/web/villany-atesz/stock",
    VILLANY_ATESZ_TOOLBOX_PAGE: "/web/villany-atesz/toolbox",

    //SkyXplore Lobby Settings limits
    MIN_PLAYERS_PER_SOLAR_SYSTEM: 1,
    MAX_PLAYERS_PER_SOLAR_SYSTEM: 5,
    ADDITIONAL_SOLAR_SYSTEMS_MIN: 0,
    ADDITIONAL_SOLAR_SYSTEMS_MAX: 30,
    PLANETS_PER_SOLAR_SYSTEM_MIN: 0,
    PLANETS_PER_SOLAR_SYSTEM_MAX: 10,
    PLANET_SIZE_MIN: 10,
    PLANET_SIZE_MAX: 20,

    //File size
    KILOBYTES: KILOBYTES,
    MEGABYTES: MEGABYTES,
    GIGABYTES: GIGABYTES,

    SKYXPLORE_LOBBY_TYPE_NEW: "NEW_GAME",
    SKYXPLORE_LOBBY_TYPE_LOAD: "LOAD_GAME",

    //Memory monitoring
    PIXEL_PER_REPORT: 3,
    GRAPH_BORDER: 5,
    GRAPH_HEIGHT: 200,
}

export default Constants;