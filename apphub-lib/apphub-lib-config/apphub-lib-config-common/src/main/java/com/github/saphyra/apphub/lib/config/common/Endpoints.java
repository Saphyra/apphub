package com.github.saphyra.apphub.lib.config.common;

public class Endpoints {
    //PAGES
    public static final String INDEX_PAGE = "/web";
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/disabled-role-management";
    public static final String ADMIN_PANEL_ERROR_REPORT_PAGE = "/web/admin-panel/error-report";
    public static final String ADMIN_PANEL_BAN_PAGE = "/web/admin-panel/ban";
    public static final String ADMIN_PANEL_ROLES_FOR_ALL_PAGE = "/web/admin-panel/roles-for-all";
    public static final String ERROR_PAGE = "/web/error";
    public static final String NOTEBOOK_PAGE = "/web/notebook";
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";
    public static final String UTILS_JSON_FORMATTER_PAGE = "/web/utils/json-formatter";
    public static final String UTILS_LOG_FORMATTER_PAGE = "/web/utils/log-formatter";
    public static final String UTILS_BASE64_ENCODER_PAGE = "/web/utils/base64";
    public static final String COMMUNITY_PAGE = "/web/community";
    public static final String CALENDAR_PAGE = "/web/calendar";

    //Training
    public static final String TRAINING_BOOK_PAGE = "/web/training/{book}/{chapter}";
    public static final String TRAINING_SAMPLE_PAGE = "/web/training/{book}/sample/{sample}";
    public static final String TRAINING_SAMPLE_ENDPOINT = "/web/training/sample";
    public static final String TRAINING_SAMPLE_REST_ENDPOINT = "/api/training/sample";

    //PLATFORM
    public static final String HEALTH = "/platform/health";
    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";
    public static final String TRANSLATE_KEY = "/internal/localization/key";

    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";

    public static final String GET_OWN_USER_ID = "/user/id";

    //EVENTS
    public static final String EVENT_TRIGGER_MEMORY_STATUS_UPDATE = "/event/trigger-memory-monitoring";
    public static final String EVENT_MEMORY_MONITORING = "/event/memory-monitoring";
    public static final String EVENT_DELETE_EXPIRED_ACCESS_TOKENS = "/event/delete-expired-access-tokens";
    public static final String EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION = "/event/refresh-access-token-expiration";
    public static final String EVENT_DELETE_ACCOUNT = "/event/delete-account";
    public static final String EVENT_SKYXPLORE_LOBBY_CLEANUP = "/event/skyxplore/lobby/cleanup";
    public static final String EVENT_SKYXPLORE_GAME_CLEANUP = "/event/skyxplore/game/cleanup";
    public static final String EVENT_WEB_SOCKET_SEND_PING_EVENT = "/event/web-socket/send-ping";
    public static final String EVENT_WEB_SOCKET_CONNECTION_CLEANUP = "/event/web-socket/connection-cleanup";
    public static final String EVENT_TRIGGER_ACCOUNT_DELETION = "/event/user-data/trigger-account-deletion";
    public static final String EVENT_TRIGGER_REVOKE_EXPIRED_BANS = "/event/user-data/trigger-remove-expired-bans";
    public static final String EVENT_SKYXPLORE_DELETE_GAMES = "/event/skyxplore/data/game/delete";
    public static final String EVENT_CLEAN_UP_STORED_FILES = "/event/storage/cleanup";
    public static final String EVENT_ACCESS_TOKEN_INVALIDATED = "/event/access-token-invalidated";

    //CONNECTION SUBSCRIPTIONS
    public static final String WS_CONNECTION_ADMIN_PANEL_MEMORY_MONITORING = "/api/ws/admin-panel/monitoring/memory";
    public static final String WS_CONNECTION_SKYXPLORE_MAIN_MENU = "/api/ws/skyxplore-data/main-menu";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY = "/api/ws/skyxplore-lobby/lobby";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY_INVITATION = "/api/ws/skyxplore-lobby/invitation";
    public static final String WS_CONNECTION_SKYXPLORE_GAME = "/api/ws/skyxplore-game/game";
    public static final String WS_CONNECTION_SKYXPLORE_GAME_PLANET = "/api/ws/skyxplore-game/game/planet";
    public static final String WS_CONNECTION_SKYXPLORE_GAME_POPULATION = "/api/ws/skyxplore-game/game/population";
    public static final String WS_CONNECTION_SKYXPLORE_INTERNAL = "/allowed-internal/skyxplore";

    //ERROR REPORTING
    public static final String ADMIN_PANEL_INTERNAL_REPORT_ERROR = "/internal/admin-panel/report-error";

    public static final String ADMIN_PANEL_GET_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_GET_ERROR_REPORT = "/api/admin-panel/error-report/{id}";
    public static final String ADMIN_PANEL_DELETE_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_MARK_ERROR_REPORTS = "/api/admin-panel/error-report/mark/{status}";
    public static final String ADMIN_PANEL_DELETE_READ_ERROR_REPORTS = "/api/admin-panel/error-report/read";
    public static final String ADMIN_PANEL_ERROR_REPORT_DELETE_ALL = "/api/admin-panel/error-report/all";

    //MONITORING
    public static final String ADMIN_PANEL_INTERNAL_REPORT_MEMORY_STATUS = "/internal/api/admin-panel/monitoring/memory";

    //MIGRATION
    public static final String ADMIN_PANEL_MIGRATION_GET_TASKS = "/api/admin-panel/migration";
    public static final String ADMIN_PANEL_MIGRATION_TRIGGER_TASK = "/api/admin-panel/migration/{event}";
    public static final String ADMIN_PANEL_MIGRATION_DELETE_TASK = "/api/admin-panel/migration/{event}";

    //LOGIN
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";
    public static final String EXTEND_SESSION = "/api/user/authentication/session";

    //ACCOUNT
    public static final String ACCOUNT_REGISTER = "/api/user";
    public static final String ACCOUNT_CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String ACCOUNT_CHANGE_EMAIL = "/api/user/account/email";
    public static final String ACCOUNT_CHANGE_USERNAME = "/api/user/account/username";
    public static final String ACCOUNT_CHANGE_PASSWORD = "/api/user/account/password";
    public static final String ACCOUNT_DELETE_ACCOUNT = "/api/user/account";
    public static final String USER_DATA_SEARCH_ACCOUNT = "/api/user/accounts";
    public static final String USER_DATA_GET_ACCOUNT = "/api/user/account";

    //SETTINGS
    public static final String GET_USER_SETTINGS = "/api/user/settings/{category}";
    public static final String SET_USER_SETTINGS = "/api/user/settings";

    //BAN USER
    public static final String ACCOUNT_BAN_USER = "/api/user/ban";
    public static final String ACCOUNT_REVOKE_BAN = "/api/user/ban/{banId}";
    public static final String ACCOUNT_GET_BANS = "/api/user/ban/{userId}";
    public static final String ACCOUNT_MARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_UNMARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_BAN_SEARCH = "/api/user/ban/search";

    //USER-DATA
    public static final String USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String USER_DATA_INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";
    public static final String USER_DATA_INTERNAL_GET_ACCOUNT = "/internal/api/user/data/{userId}";
    public static final String USER_DATA_INTERNAL_USER_EXISTS = "/internal/api/user/data/{userId}/exists";

    public static final String USER_DATA_GET_USERNAME = "/api/user/data/name";
    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_ENABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";
    public static final String USER_DATA_ADD_ROLE_TO_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_REMOVE_ROLE_FROM_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_ROLES_FOR_ALL_RESTRICTED = "/api/user/data/roles/restricted";

    //MODULES
    public static final String MODULES_GET_MODULES_OF_USER = "/api/modules";
    public static final String MODULES_SET_FAVORITE = "/api/modules/{module}/favorite";

    //NOTEBOOK
    public static final String NOTEBOOK_GET_CATEGORY_TREE = "/api/notebook/category/tree";
    public static final String NOTEBOOK_CREATE_CATEGORY = "/api/notebook/category";
    public static final String NOTEBOOK_CREATE_TEXT = "/api/notebook/text";
    public static final String NOTEBOOK_GET_CHILDREN_OF_CATEGORY = "/api/notebook/category/children";
    public static final String NOTEBOOK_DELETE_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_EDIT_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_PIN_LIST_ITEM = "/api/notebook/item/{listItemId}/pin";
    public static final String NOTEBOOK_GET_PINNED_ITEMS = "/api/notebook/item/pinned";
    public static final String NOTEBOOK_GET_TEXT = "/api/notebook/text/{listItemId}";
    public static final String NOTEBOOK_EDIT_TEXT = "/api/notebook/text/{listItemId}";
    public static final String NOTEBOOK_CREATE_LINK = "/api/notebook/link";
    public static final String NOTEBOOK_CREATE_CHECKLIST = "/api/notebook/checklist";
    public static final String NOTEBOOK_EDIT_CHECKLIST = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_GET_CHECKLIST = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS = "/api/notebook/checklist/item/{checklistItemId}/status";
    public static final String NOTEBOOK_EDIT_CHECKLIST_ITEM = "/api/notebook/checklist/item/{checklistItemId}/content";
    public static final String NOTEBOOK_ADD_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}/item";
    public static final String NOTEBOOK_DELETE_CHECKLIST_ITEM = "/api/notebook/checklist/item/{checklistItemId}";
    public static final String NOTEBOOK_CREATE_TABLE = "/api/notebook/table";
    public static final String NOTEBOOK_EDIT_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_GET_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_CLONE_LIST_ITEM = "/api/notebook/{listItemId}/clone";
    public static final String NOTEBOOK_CHECKLIST_DELETE_CHECKED = "/api/notebook/checklist/{listItemId}/checked";
    public static final String NOTEBOOK_ORDER_CHECKLIST_ITEMS = "/api/notebook/checklist/{listItemId}/order";
    public static final String NOTEBOOK_SEARCH = "/api/notebook/item/search";
    public static final String NOTEBOOK_ARCHIVE_ITEM = "/api/notebook/item/{listItemId}/archive";
    public static final String NOTEBOOK_CREATE_ONLY_TITLE = "/api/notebook/only-title";
    public static final String NOTEBOOK_MOVE_LIST_ITEM = "/api/notebook/{listItemId}/move";
    public static final String NOTEBOOK_CREATE_IMAGE = "/api/notebook/image";
    public static final String NOTEBOOK_CREATE_FILE = "/api/notebook/file";
    public static final String NOTEBOOK_GET_LIST_ITEM = "/api/notebook/list-item/{listItemId}";
    public static final String NOTEBOOK_TABLE_SET_ROW_STATUS = "/api/notebook/table/row/{rowId}/status";
    public static final String NOTEBOOK_TABLE_DELETE_CHECKED = "/api/notebook/table/{listItemId}/checked";
    public static final String NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS = "/api/notebook/table/column/{columnId}/status";
    public static final String NOTEBOOK_CREATE_PIN_GROUP = "/api/notebook/pin-group";
    public static final String NOTEBOOK_GET_PIN_GROUPS = "/api/notebook/pin-group";
    public static final String NOTEBOOK_DELETE_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}";
    public static final String NOTEBOOK_RENAME_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}";
    public static final String NOTEBOOK_ADD_ITEM_TO_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}/add/{listItemId}";
    public static final String NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP = "/api/notebook/pin-group/{pinGroupId}/remove/{listItemId}";
    public static final String NOTEBOOK_PIN_GROUP_OPENED = "/api/notebook/pin-group/{pinGroupId}";

    //UTILS
    public static final String UTILS_LOG_FORMATTER_GET_VISIBILITY = "/api/utils/log-formatter/visibility";
    public static final String UTILS_LOG_FORMATTER_SET_VISIBILITY = "/api/utils/log-formatter/visibility";

    //SKYXPLORE-DATA
    public static final String SKYXPLORE_INTERNAL_GET_CHARACTER_BY_USER_ID = "/allowed-internal/skyxplore/data/character/{userId}";
    public static final String SKYXPLORE_INTERNAL_SAVE_GAME_DATA = "/allowed-internal/skyxplore/data/game/data";
    public static final String SKYXPLORE_INTERNAL_DELETE_GAME_ITEM = "/internal/skyxplore/data/game-item";
    public static final String SKYXPLORE_INTERNAL_GET_GAME_MODEL = "/internal/skyxplore/game/{gameId}";

    //SKYXPLORE-DATA CHARACTER
    public static final String SKYXPLORE_CHARACTER_EXISTS = "/api/skyxplore/data/character/exists";
    public static final String SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = "/api/skyxplore/data/character";
    public static final String SKYXPLORE_GET_CHARACTER_NAME = "/api/skyxplore/data/character/name";

    //SKYXPLORE-DATA GAMES
    public static final String SKYXPLORE_GET_GAMES = "/api/skyxplore/data/saved-game";
    public static final String SKYXPLORE_DELETE_GAME = "/api/skyxplore/data/saved-game/{gameId}";

    //SKYXPLORE-DATA-FRIENDS
    public static final String SKYXPLORE_SEARCH_FOR_FRIENDS = "/api/skyxplore/data/friend/candidate";
    public static final String SKYXPLORE_ADD_FRIEND = "/api/skyxplore/data/friend/request";
    public static final String SKYXPLORE_GET_SENT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/sent";
    public static final String SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/incoming";
    public static final String SKYXPLORE_CANCEL_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_ACCEPT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_GET_FRIENDS = "/api/skyxplore/data/friend";
    public static final String SKYXPLORE_REMOVE_FRIEND = "/api/skyxplore/data/friend/{friendshipId}";

    //SKYXPLORE-DATA RESOURCES
    public static final String SKYXPLORE_GET_ITEM_DATA = "/api/skyxplore/data/data/{dataId}";
    public static final String SKYXPLORE_DATA_AVAILABLE_BUILDINGS = "/api/skyxplore/data/data/{surfaceType}/buildings";
    public static final String SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS = "/api/skyxplore/data/citizen/stats-and-skills";

    //SKYXPLORE-LOBBY
    public static final String SKYXPLORE_INTERNAL_GAME_LOADED = "/allowed-internal/skyxplore/lobby/{gameId}/loaded";

    public static final String SKYXPLORE_LOBBY_VIEW_FOR_PAGE = "/api/skyxplore/lobby/page";
    public static final String SKYXPLORE_LOBBY_IS_IN_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_PLAYERS = "/api/skyxplore/lobby/players";
    public static final String SKYXPLORE_LOBBY_GET_ALLIANCES = "/api/skyxplore/lobby/alliances";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_START_GAME = "/api/skyxplore/lobby/start";
    public static final String SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS = "/api/skyxplore/lobby/friends/active";
    public static final String SKYXPLORE_LOBBY_LOAD_GAME = "/api/skyxplore/lobby/load-game/{gameId}";
    public static final String SKYXPLORE_LOBBY_EDIT_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_LOBBY_GET_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_REMOVE_AI = "/api/skyxplore/lobby/ai/{userId}";
    public static final String SKYXPLORE_LOBBY_GET_AIS = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER = "/api/skyxplore/lobby/alliance/player/{userId}";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI = "/api/skyxplore/lobby/alliance/ai/{userId}";

    //SKYXPLORE-GAME
    public static final String SKYXPLORE_INTERNAL_CREATE_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_LOAD_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION = "/internal/skyxplore/game/{gameId}/load-preview";
    public static final String SKYXPLORE_INTERNAL_DELETE_GAME = "/internal/skyxplore/game/{gameId}";

    public static final String SKYXPLORE_GAME_SAVE = "/api/skyxplore/game";
    public static final String SKYXPLORE_GAME_IS_HOST = "/api/skyxplore/game/host";
    public static final String SKYXPLORE_IS_USER_IN_GAME = "/api/skyxplore/game";
    public static final String SKYXPLORE_GAME_PAUSE = "/api/skyxplore/game/pause";
    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";
    public static final String SKYXPLORE_GAME_GET_PLAYERS = "/api/skyxplore/game/player";
    public static final String SKYXPLORE_GAME_CREATE_CHAT_ROOM = "/api/skyxplore/game/chat/room";
    public static final String SKYXPLORE_GAME_LEAVE_CHAT_ROOM = "/api/skyxplore/game/chat/room/{roomId}";
    public static final String SKYXPLORE_GAME_GET_CHAT_ROOMS = "/api/skyxplore/game/chat/room";

    public static final String SKYXPLORE_GET_SOLAR_SYSTEM = "/api/skyxplore/game/solar-system/{solarSystemId}";
    public static final String SKYXPLORE_PLANET_GET_OVERVIEW = "/api/skyxplore/game/planet/{planetId}/overview";
    public static final String SKYXPLORE_PLANET_GET_STORAGE_SETTINGS = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_CREATE_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_DELETE_STORAGE_SETTING = "/api/skyxplore/game/storage-settings/{storageSettingId}";
    public static final String SKYXPLORE_PLANET_EDIT_STORAGE_SETTING = "/api/skyxplore/game/storage-settings";
    public static final String SKYXPLORE_PLANET_GET_POPULATION = "/api/skyxplore/game/planet/{planetId}/citizen";
    public static final String SKYXPLORE_PLANET_RENAME_CITIZEN = "/api/skyxplore/game/citizen/{citizenId}/rename";
    public static final String SKYXPLORE_PLANET_UPDATE_PRIORITY = "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}";
    public static final String SKYXPLORE_PLANET_RENAME = "/api/skyxplore/game/planet/{planetId}/name";
    public static final String SKYXPLORE_SOLAR_SYSTEM_RENAME = "/api/skyxplore/game/solar-system/{solarSystemId}/name";
    public static final String SKYXPLORE_EXIT_GAME = "/api/skyxplore/game";

    public static final String SKYXPLORE_BUILDING_CONSTRUCT_NEW = "/api/skyxplore/game/building/{planetId}/{surfaceId}";
    public static final String SKYXPLORE_BUILDING_UPGRADE = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    public static final String SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    public static final String SKYXPLORE_BUILDING_DECONSTRUCT = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";
    public static final String SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";

    public static final String SKYXPLORE_GAME_TERRAFORM_SURFACE = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_GAME_CANCEL_TERRAFORMATION = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES = "/api/skyxplore/data/{surfaceType}/terraforming-possibilities";
    public static final String SKYXPLORE_DATA_RESOURCES = "/api/skyxplore/data/resources";

    public static final String SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY = "/api/skyxplore/game/{planetId}/{type}/{itemId}/priority";
    public static final String SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM = "/api/skyxplore/game/{planetId}/{type}/{itemId}";

    //COMMUNITY
    public static final String COMMUNITY_BLACKLIST_SEARCH = "/api/community/blacklist/search";
    public static final String COMMUNITY_GET_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_CREATE_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_DELETE_BLACKLIST = "/api/community/blacklist/{blacklistId}";
    public static final String COMMUNITY_GET_SENT_FRIEND_REQUESTS = "/api/community/friend-request/sent";
    public static final String COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS = "/api/community/friend-request/received";
    public static final String COMMUNITY_FRIEND_REQUEST_SEARCH = "/api/community/friend-request/search";
    public static final String COMMUNITY_FRIEND_REQUEST_CREATE = "/api/community/friend-request";
    public static final String COMMUNITY_FRIEND_REQUEST_DELETE = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_FRIEND_REQUEST_ACCEPT = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_GET_FRIENDS = "/api/community/friendship";
    public static final String COMMUNITY_DELETE_FRIENDSHIP = "/api/community/friendship/{friendshipId}";
    public static final String COMMUNITY_GET_GROUPS = "/api/community/group";
    public static final String COMMUNITY_GROUP_CREATE = "/api/community/group";
    public static final String COMMUNITY_GROUP_RENAME = "/api/community/group/{groupId}/name";
    public static final String COMMUNITY_GROUP_GET_MEMBERS = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES = "/api/community/group/{groupId}/member/search";
    public static final String COMMUNITY_GROUP_CREATE_MEMBER = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_CHANGE_INVITATION_TYPE = "/api/community/group/{groupId}/invitation-type";
    public static final String COMMUNITY_GROUP_DELETE_MEMBER = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_MEMBER_ROLES = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_DELETE = "/api/community/group/{groupId}";
    public static final String COMMUNITY_GROUP_CHANGE_OWNER = "/api/community/group/{groupId}/owner";

    //CALENDAR
    public static final String CALENDAR_GET_CALENDAR = "/api/calendar/calendar";
    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/event";
    public static final String CALENDAR_OCCURRENCE_EDIT = "/api/calendar/occurrence/{occurrenceId}/edit";
    public static final String CALENDAR_OCCURRENCE_DONE = "/api/calendar/occurrence/{occurrenceId}/done";
    public static final String CALENDAR_OCCURRENCE_DEFAULT = "/api/calendar/occurrence/{occurrenceId}/default";
    public static final String CALENDAR_OCCURRENCE_SNOOZED = "/api/calendar/occurrence/{occurrenceId}/snoozed";
    public static final String CALENDAR_EVENT_DELETE = "/api/calendar/event/{eventId}";
    public static final String CALENDAR_SEARCH = "/api/calendar/search";

    //Encryption
    public static final String ENCRYPTION_INTERNAL_CREATE_KEY = "/internal/encryption/key/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_DELETE_KEY = "/internal/encryption/key/{dataType}/{externalId}/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_GET_KEY = "/internal/encryption/key/{dataType}/{externalId}/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_GET_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";
    public static final String ENCRYPTION_INTERNAL_CREATE_SHARED_DATA = "/internal/encryption/shared-data";
    public static final String ENCRYPTION_INTERNAL_CLONE_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";
    public static final String ENCRYPTION_INTERNAL_DELETE_SHARED_DATA_ENTITY = "/internal/encryption/shared-date/{sharedDataId}";
    public static final String ENCRYPTION_INTERNAL_DELETE_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";

    //Storage
    public static final String STORAGE_UPLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_DELETE_FILE = "/internal/storage/{storedFileId}";
    public static final String STORAGE_DOWNLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_CREATE_FILE = "/internal/storage";
    public static final String STORAGE_GET_METADATA = "/api/storage/{storedFileId}/metadata";

    //VillanyAtesz
    public static final String VILLANY_ATESZ_CREATE_CONTACT = "/api/villany-atesz/contact";
    public static final String VILLANY_ATESZ_EDIT_CONTACT = "/api/villany-atesz/contact/{contactId}";
    public static final String VILLANY_ATESZ_DELETE_CONTACT = "/api/villany-atesz/contact/{contactId}";
    public static final String VILLANY_ATESZ_GET_CONTACTS = "/api/villany-atesz/contact";
    public static final String VILLANY_ATESZ_CREATE_STOCK_CATEGORY = "/api/villany-atesz/stock/category";
    public static final String VILLANY_ATESZ_EDIT_STOCK_CATEGORY = "/api/villany-atesz/stock/category/{stockCategoryId}";
    public static final String VILLANY_ATESZ_DELETE_STOCK_CATEGORY = "/api/villany-atesz/stock/category/{stockCategoryId}";
    public static final String VILLANY_ATESZ_GET_STOCK_CATEGORIES = "/api/villany-atesz/stock/category";
    public static final String VILLANY_ATESZ_CREATE_STOCK_ITEM = "/api/villany-atesz/stock/item";
    public static final String VILLANY_ATESZ_DELETE_STOCK_ITEM = "/api/villany-atesz/stock/item/{stockItemId}";
    public static final String VILLANY_ATESZ_GET_STOCK_ITEMS = "/api/villany-atesz/stock/item";
    public static final String VILLANY_ATESZ_CREATE_CART = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_GET_CARTS = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_GET_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_ADD_TO_CART = "/api/villany-atesz/cart";
    public static final String VILLANY_ATESZ_FINALIZE_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_DELETE_CART = "/api/villany-atesz/cart/{cartId}";
    public static final String VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY = "/api/villany-atesz/stock/item/{stockCategoryId}";
    public static final String VILLANY_ATESZ_STOCK_ACQUIRE = "/api/villany-atesz/stock/acquire";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS = "/api/villany-atesz/stock/inventory";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME = "/api/villany-atesz/stock/inventory/{stockItemId}/name";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY = "/api/villany-atesz/stock/inventory/{stockItemId}/category";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER = "/api/villany-atesz/stock/inventory/{stockItemId}/serial-number";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR = "/api/villany-atesz/stock/inventory/{stockItemId}/in-car";
    public static final String VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE = "/api/villany-atesz/stock/inventory/{stockItemId}/in-storage";
}