window.Mapping = new function(){
    this.INDEX_PAGE = "/web";
    this.MODULES_PAGE = "/web/modules";
    this.SKYXPLORE_PAGE = "/web/skyxplore";
    this.SKYXPLORE_LOBBY_PAGE = "/web/skyxplore/lobby";
    this.SKYXPLORE_GAME_PAGE = "/web/skyxplore/game";

    const endpoints = {
        //WS Connection Subscriptions
        WS_CONNECTION_SKYXPLORE_MAIN_MENU: new Endpoint("/api/message-sender/skyxplore/main-menu", HttpMethod.GET),
        WS_CONNECTION_SKYXPLORE_LOBBY: new Endpoint("/api/message-sender/skyxplore/lobby", HttpMethod.GET),
        WS_CONNECTION_SKYXPLORE_GAME: new Endpoint("/api/message-sender/skyxplore/game", HttpMethod.GET),
        WS_CONNECTION_ADMIN_PANEL_MONITORING: new Endpoint("/api/message-sender/admin-panel/monitoring", HttpMethod.GET),
        WS_CONNECTION_ADMIN_PANEL_ERROR_REPORT: new Endpoint("/api/message-sender/admin-panel/error-report", HttpMethod.GET),

        //Error reporting
        ADMIN_PANEL_GET_ERROR_REPORTS: new Endpoint("/api/admin-panel/error-report", HttpMethod.POST),
        ADMIN_PANEL_GET_ERROR_REPORT: new Endpoint("/api/admin-panel/error-report/{id}", HttpMethod.GET),
        ADMIN_PANEL_DELETE_ERROR_REPORTS: new Endpoint("/api/admin-panel/error-report", HttpMethod.DELETE),
        ADMIN_PANEL_MARK_ERROR_REPORTS: new Endpoint("/api/admin-panel/error-report/mark/{status}", HttpMethod.POST),
        ADMIN_PANEL_DELETE_READ_ERROR_REPORTS: new Endpoint("/api/admin-panel/error-report/read", HttpMethod.DELETE),
        ADMIN_PANEL_ERROR_REPORT_DELETE_ALL: new Endpoint("/api/admin-panel/error-report/all", HttpMethod.DELETE),

        //Login
        LOGIN: new Endpoint("/api/user/authentication/login", HttpMethod.POST),
        LOGOUT: new Endpoint("/api/user/authentication/logout", HttpMethod.POST),
        CHECK_SESSION: new Endpoint("/api/user/authentication/session", HttpMethod.GET),
        EXTEND_SESSION: new Endpoint("/api/user/authentication/session", HttpMethod.POST),

        //Account
        ACCOUNT_REGISTER: new Endpoint("/api/user", HttpMethod.POST),
        ACCOUNT_CHANGE_LANGUAGE: new Endpoint("/api/user/account/language", HttpMethod.POST),
        ACCOUNT_CHANGE_EMAIL: new Endpoint("/api/user/account/email", HttpMethod.POST),
        ACCOUNT_CHANGE_USERNAME: new Endpoint("/api/user/account/username", HttpMethod.POST),
        ACCOUNT_CHANGE_PASSWORD: new Endpoint("/api/user/account/password", HttpMethod.POST),
        ACCOUNT_DELETE_ACCOUNT: new Endpoint("/api/user/account", HttpMethod.DELETE),
        ACCOUNT_GET_LANGUAGES: new Endpoint("/api/user/data/languages", HttpMethod.GET),

        //Ban
        ACCOUNT_GET_BANS: new Endpoint("/api/user/ban/{userId}", HttpMethod.GET),
        ACCOUNT_BAN_USER: new Endpoint("/api/user/ban", HttpMethod.PUT),
        ACCOUNT_REMOVE_BAN: new Endpoint("/api/user/ban/{banId}", HttpMethod.DELETE),
        ACCOUNT_MARK_FOR_DELETION: new Endpoint("/api/user/{userId}/mark-for-deletion", HttpMethod.DELETE),
        ACCOUNT_UNMARK_FOR_DELETION: new Endpoint("/api/user/{userId}/mark-for-deletion", HttpMethod.POST),

        //User data
        USER_DATA_GET_USER_ROLES: new Endpoint("/api/user/data/roles", HttpMethod.POST),
        USER_DATA_ADD_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.PUT),
        USER_DATA_REMOVE_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.DELETE),
        USER_DATA_DISABLE_ROLE: new Endpoint("/api/user/data/roles/{role}", HttpMethod.PUT),
        USER_DATA_ENABLE_ROLE: new Endpoint("/api/user/data/roles/{role}", HttpMethod.DELETE),
        USER_DATA_GET_DISABLED_ROLES: new Endpoint("/api/user/data/roles/disabled", HttpMethod.GET),
        USER_DATA_SEARCH_ACCOUNT: new Endpoint("/api/user/accounts", HttpMethod.POST),
        USER_DATA_ROLES_FOR_ALL_RESTRICTED: new Endpoint("/api/user/data/roles/restricted", HttpMethod.GET),
        USER_DATA_ADD_ROLE_TO_ALL: new Endpoint("/api/user/data/roles/all/{role}", HttpMethod.POST),
        USER_DATA_REMOVE_ROLE_FROM_ALL: new Endpoint("/api/user/data/roles/all/{role}", HttpMethod.DELETE),

        //Modules
        MODULES_GET_MODULES_OF_USER: new Endpoint("/api/modules", HttpMethod.GET),
        MODULES_SET_FAVORITE: new Endpoint("/api/modules/{module}/favorite", HttpMethod.POST),

        //Admin panel
        ADMIN_PANEL_MENU: new Endpoint("/res/admin-panel/json/index_menu.json", HttpMethod.GET),
        ADMIN_PANEL_AVAILABLE_ROLES: new Endpoint("/res/admin-panel/json/available_roles.json", HttpMethod.GET),

        //Notebook
        NOTEBOOK_GET_CATEGORY_TREE: new Endpoint("/api/notebook/category/tree", HttpMethod.GET),
        NOTEBOOK_CREATE_CATEGORY: new Endpoint("/api/notebook/category", HttpMethod.PUT),
        NOTEBOOK_CREATE_TEXT: new Endpoint("/api/notebook/text", HttpMethod.PUT),
        NOTEBOOK_GET_CHILDREN_OF_CATEGORY: new Endpoint("/api/notebook/category/children", HttpMethod.GET),
        NOTEBOOK_DELETE_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}", HttpMethod.DELETE),
        NOTEBOOK_EDIT_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}", HttpMethod.POST),
        NOTEBOOK_PIN_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}/pin", HttpMethod.POST),
        NOTEBOOK_GET_PINNED_ITEMS: new Endpoint("/api/notebook/item/pinned", HttpMethod.GET),
        NOTEBOOK_GET_TEXT: new Endpoint("/api/notebook/text/{listItemId}", HttpMethod.GET),
        NOTEBOOK_EDIT_TEXT: new Endpoint("/api/notebook/text/{listItemId}", HttpMethod.POST),
        NOTEBOOK_CREATE_LINK: new Endpoint("/api/notebook/link", HttpMethod.PUT),
        NOTEBOOK_CREATE_CHECKLIST_ITEM: new Endpoint("/api/notebook/checklist", HttpMethod.PUT),
        NOTEBOOK_EDIT_CHECKLIST_ITEM: new Endpoint("/api/notebook/checklist/{listItemId}", HttpMethod.POST),
        NOTEBOOK_GET_CHECKLIST_ITEM: new Endpoint("/api/notebook/checklist/{listItemId}", HttpMethod.GET),
        NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS: new Endpoint("/api/notebook/checklist/item/{checklistItemId}/status", HttpMethod.POST),
        NOTEBOOK_CREATE_TABLE: new Endpoint("/api/notebook/table", HttpMethod.PUT),
        NOTEBOOK_EDIT_TABLE: new Endpoint("/api/notebook/table/{listItemId}", HttpMethod.POST),
        NOTEBOOK_GET_TABLE: new Endpoint("/api/notebook/table/{listItemId}", HttpMethod.GET),
        NOTEBOOK_CLONE_LIST_ITEM: new Endpoint("/api/notebook/{listItemId}/clone", HttpMethod.POST),
        NOTEBOOK_CREATE_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table", HttpMethod.PUT),
        NOTEBOOK_EDIT_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}", HttpMethod.POST),
        NOTEBOOK_GET_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}", HttpMethod.GET),
        NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS: new Endpoint("/api/notebook/checklist-table/{listItemId}/{rowIndex}", HttpMethod.POST),
        NOTEBOOK_CONVERT_TABLE_TO_CHECKLIST_TABLE: new Endpoint("/api/notebook/table/{listItemId}/convert-to-checklist-table", HttpMethod.POST),
        NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST: new Endpoint("/api/notebook/checklist/{listItemId}/checked", HttpMethod.DELETE),
        NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}/checked", HttpMethod.DELETE),
        NOTEBOOK_ORDER_CHECKLIST_ITEMS: new Endpoint("/api/notebook/checklist/{listItemId}/order", HttpMethod.POST),
        NOTEBOOK_SEARCH: new Endpoint("/api/notebook/item/search", HttpMethod.POST),
        NOTEBOOK_ARCHIVE_ITEM: new Endpoint("/api/notebook/item/{listItemId}/archive", HttpMethod.POST),
        NOTEBOOK_CREATE_ONLY_TITLE: new Endpoint("/api/notebook/only-title", HttpMethod.PUT),
        NOTEBOOK_MOVE_LIST_ITEM: new Endpoint("/api/notebook/{listItemId}/move", HttpMethod.POST),
        NOTEBOOK_CREATE_IMAGE: new Endpoint("/api/notebook/image", HttpMethod.PUT),
        NOTEBOOK_CREATE_FILE: new Endpoint("/api/notebook/file", HttpMethod.PUT),

        //Utils
        UTILS_LOG_FORMATTER_GET_VISIBILITY: new Endpoint("/api/utils/log-formatter/visibility", HttpMethod.PUT),
        UTILS_LOG_FORMATTER_SET_VISIBILITY: new Endpoint("/api/utils/log-formatter/visibility", HttpMethod.POST),

        //SkyXplore data
        SKYXPLORE_CREATE_OR_UPDATE_CHARACTER: new Endpoint("/api/skyxplore/data/character", HttpMethod.POST),
        SKYXPLORE_GET_GAMES: new Endpoint("/api/skyxplore/data/saved-game", HttpMethod.GET),
        SKYXPLORE_DELETE_GAME: new Endpoint("/api/skyxplore/data/saved-game/{gameId}", HttpMethod.DELETE),
        SKYXPLORE_SEARCH_FOR_FRIENDS: new Endpoint("/api/skyxplore/data/friend/candidate", HttpMethod.POST),
        SKYXPLORE_ADD_FRIEND: new Endpoint("/api/skyxplore/data/friend/request", HttpMethod.PUT),
        SKYXPLORE_GET_SENT_FRIEND_REQUEST: new Endpoint("/api/skyxplore/data/friend/request/sent", HttpMethod.GET),
        SKYXPLORE_GET_INCOMING_FRIEND_REQUEST: new Endpoint("/api/skyxplore/data/friend/request/incoming", HttpMethod.GET),
        SKYXPLORE_CANCEL_FRIEND_REQUEST: new Endpoint("/api/skyxplore/data/friend/request/{friendRequestId}", HttpMethod.DELETE),
        SKYXPLORE_ACCEPT_FRIEND_REQUEST: new Endpoint("/api/skyxplore/data/friend/request/{friendRequestId}", HttpMethod.POST),
        SKYXPLORE_GET_FRIENDS: new Endpoint("/api/skyxplore/data/friend", HttpMethod.GET),
        SKYXPLORE_REMOVE_FRIEND: new Endpoint("/api/skyxplore/data/friend/{friendshipId}", HttpMethod.DELETE),
        SKYXPLORE_GET_CHARACTER: new Endpoint("/api/skyxplore/data/character", HttpMethod.GET),
        SKYXPLORE_GET_ITEM_DATA: new Endpoint("/api/skyxplore/data/data/{dataId}", HttpMethod.GET),
        SKYXPLORE_DATA_AVAILABLE_BUILDINGS: new Endpoint("/api/skyxplore/data/data/{surfaceType}/buildings", HttpMethod.GET),

        //SkyXplore lobby
        SKYXPLORE_EXIT_FROM_LOBBY: new Endpoint("/api/skyxplore/lobby", HttpMethod.DELETE),
        SKYXPLORE_INVITE_TO_LOBBY: new Endpoint("/api/skyxplore/lobby/invite/{friendId}", HttpMethod.POST),
        SKYXPLORE_LOBBY_GET_MEMBERS: new Endpoint("/api/skyxplore/lobby/members", HttpMethod.GET),
        SKYXPLORE_CREATE_LOBBY: new Endpoint("/api/skyxplore/lobby", HttpMethod.PUT),
        SKYXPLORE_LOBBY_ACCEPT_INVITATION: new Endpoint("/api/skyxplore/lobby/join/{invitorId}", HttpMethod.POST),
        SKYXPLORE_LOBBY_GET_GAME_SETTINGS: new Endpoint("/api/skyxplore/lobby/settings", HttpMethod.GET),
        SKYXPLORE_START_GAME: new Endpoint("/api/skyxplore/lobby/start", HttpMethod.POST),
        SKYXPLORE_GET_ACTIVE_FRIENDS: new Endpoint("/api/skyxplore/lobby/friends/active", HttpMethod.GET),
        SKYXPLORE_LOBBY_LOAD_GAME: new Endpoint("/api/skyxplore/lobby/load-game/{gameId}", HttpMethod.POST),

        //SkyXplore game
        SKYXPLORE_GET_UNIVERSE: new Endpoint("/api/skyxplore/game/universe", HttpMethod.GET),
        SKYXPLORE_GAME_GET_PLAYERS: new Endpoint("/api/skyxplore/game/player", HttpMethod.GET),
        SKYXPLORE_GAME_CREATE_CHAT_ROOM: new Endpoint("/api/skyxplore/game/chat/room", HttpMethod.PUT),
        SKYXPLORE_GAME_LEAVE_ROOM: new Endpoint("/api/skyxplore/game/chat/room/{roomId}", HttpMethod.DELETE),
        SKYXPLORE_GET_SOLAR_SYSTEM: new Endpoint("/api/skyxplore/game/solar-system/{solarSystemId}", HttpMethod.GET),
        SKYXPLORE_GET_PLANET_SURFACE: new Endpoint("/api/skyxplore/game/planet/{planetId}/surface", HttpMethod.GET),
        SKYXPLORE_GET_PLANET_STORAGE: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage", HttpMethod.GET),
        SKYXPLORE_GET_PLANET_OVERVIEW: new Endpoint("/api/skyxplore/game/planet/{planetId}/overview", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_POPULATION_OVERVIEW: new Endpoint("/api/skyxplore/game/planet/{planetId}/population", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_BUILDING_OVERVIEW: new Endpoint("/api/skyxplore/game/planet/{planetId}/building", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_PRIORITIES: new Endpoint("/api/skyxplore/game/planet/{planetId}/priority", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_STORAGE_SETTINGS: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings", HttpMethod.GET),
        SKYXPLORE_PLANET_CREATE_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings", HttpMethod.PUT),
        SKYXPLORE_PLANET_DELETE_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings/{storageSettingId}", HttpMethod.DELETE),
        SKYXPLORE_PLANET_EDIT_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings", HttpMethod.POST),
        SKYXPLORE_PLANET_GET_POPULATION: new Endpoint("/api/skyxplore/game/planet/{planetId}/citizen", HttpMethod.GET),
        SKYXPLORE_PLANET_RENAME_CITIZEN: new Endpoint("/api/skyxplore/game/planet/{planetId}/citizen/{citizenId}/rename", HttpMethod.POST),
        SKYXPLORE_PLANET_UPDATE_PRIORITY: new Endpoint("/api/skyxplore/game/planet/{planetId}/priority/{priorityType}", HttpMethod.POST),
        SKYXPLORE_PLANET_RENAME: new Endpoint("/api/skyxplore/game/planet/{planetId}/name", HttpMethod.POST),
        SKYXPLORE_SOLAR_SYSTEM_RENAME: new Endpoint("/api/skyxplore/game/solar-system/{solarSystemId}/name", HttpMethod.POST),
        SKYXPLORE_EXIT_GAME: new Endpoint("/api/skyxplore/game", HttpMethod.DELETE),
        SKYXPLORE_BUILDING_CONSTRUCT_NEW: new Endpoint("/api/skyxplore/game/building/{planetId}/{surfaceId}", HttpMethod.PUT),
        SKYXPLORE_BUILDING_UPGRADE: new Endpoint("/api/skyxplore/game/building/{planetId}/{buildingId}", HttpMethod.POST),
        SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION: new Endpoint("/api/skyxplore/game/building/{planetId}/{buildingId}", HttpMethod.DELETE),
        SKYXPLORE_BUILDING_DECONSTRUCT: new Endpoint("/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct", HttpMethod.POST),
        SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION: new Endpoint("/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct", HttpMethod.DELETE),
        SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES: new Endpoint("/api/skyxplore/data/{surfaceType}/terraforming-possibilities", HttpMethod.GET),
        SKYXPLORE_GAME_TERRAFORM_SURFACE: new Endpoint("/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform", HttpMethod.POST),
        SKYXPLORE_GAME_CANCEL_TERRAFORMATION: new Endpoint("/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform", HttpMethod.DELETE),
        SKYXPLORE_PLANET_GET_QUEUE: new Endpoint("/api/skyxplore/game/{planetId}/queue", HttpMethod.GET),
        SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY: new Endpoint("/api/skyxplore/game/{planetId}/{type}/{itemId}/priority", HttpMethod.POST),
        SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM: new Endpoint("/api/skyxplore/game/{planetId}/{type}/{itemId}", HttpMethod.DELETE),
        SKYXPLORE_GAME_PAUSE: new Endpoint("/api/skyxplore/game/pause", HttpMethod.POST),

        //COMMUNITY
        COMMUNITY_GET_FRIENDS: new Endpoint("/api/community/friendship", HttpMethod.GET),
        COMMUNITY_FRIEND_REQUEST_SEARCH: new Endpoint("/api/community/friend-request/search", HttpMethod.POST),
        COMMUNITY_FRIEND_REQUEST_CREATE: new Endpoint("/api/community/friend-request", HttpMethod.PUT),
        COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS: new Endpoint("/api/community/friend-request/received", HttpMethod.GET),
        COMMUNITY_GET_SENT_FRIEND_REQUESTS: new Endpoint("/api/community/friend-request/sent", HttpMethod.GET),
        COMMUNITY_FRIEND_REQUEST_DELETE: new Endpoint("/api/community/friend-request/{friendRequestId}", HttpMethod.DELETE),
        COMMUNITY_FRIEND_REQUEST_ACCEPT: new Endpoint("/api/community/friend-request/{friendRequestId}", HttpMethod.POST),
        COMMUNITY_DELETE_FRIENDSHIP: new Endpoint("/api/community/friendship/{friendshipId}", HttpMethod.DELETE),
        COMMUNITY_BLACKLIST_SEARCH: new Endpoint("/api/community/blacklist/search", HttpMethod.POST),
        COMMUNITY_CREATE_BLACKLIST: new Endpoint("/api/community/blacklist", HttpMethod.PUT),
        COMMUNITY_GET_BLACKLIST: new Endpoint("/api/community/blacklist", HttpMethod.GET),
        COMMUNITY_DELETE_BLACKLIST: new Endpoint("/api/community/blacklist/{blacklistId}", HttpMethod.DELETE),
        COMMUNITY_GROUP_CREATE: new Endpoint("/api/community/group", HttpMethod.PUT),
        COMMUNITY_GET_GROUPS: new Endpoint("/api/community/group", HttpMethod.GET),
        COMMUNITY_GROUP_RENAME: new Endpoint("/api/community/group/{groupId}/name", HttpMethod.POST),
        COMMUNITY_GROUP_CHANGE_INVITATION_TYPE: new Endpoint("/api/community/group/{groupId}/invitation-type", HttpMethod.POST),
        COMMUNITY_GROUP_GET_MEMBERS: new Endpoint("/api/community/group/{groupId}/member", HttpMethod.GET),
        COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES: new Endpoint("/api/community/group/{groupId}/member/search", HttpMethod.POST),
        COMMUNITY_GROUP_CREATE_MEMBER: new Endpoint("/api/community/group/{groupId}/member", HttpMethod.PUT),
        COMMUNITY_GROUP_DELETE_MEMBER: new Endpoint("/api/community/group/{groupId}/member/{groupMemberId}", HttpMethod.DELETE),
        COMMUNITY_GROUP_MEMBER_ROLES: new Endpoint("/api/community/group/{groupId}/member/{groupMemberId}", HttpMethod.POST),
        COMMUNITY_GROUP_DELETE: new Endpoint("/api/community/group/{groupId}", HttpMethod.DELETE),
        COMMUNITY_GROUP_CHANGE_OWNER: new Endpoint("/api/community/group/{groupId}/owner", HttpMethod.POST),

        //DIARY
        DIARY_GET_CALENDAR: new Endpoint("/api/diary/calendar", HttpMethod.GET),
        DIARY_CREATE_EVENT: new Endpoint("/api/diary/event", HttpMethod.PUT),
        DIARY_OCCURRENCE_EDIT: new Endpoint("/api/diary/occurrence/{occurrenceId}/edit", HttpMethod.POST),
        DIARY_EVENT_DELETE: new Endpoint("/api/diary/event/{eventId}", HttpMethod.DELETE),
        DIARY_OCCURRENCE_DONE: new Endpoint("/api/diary/occurrence/{occurrenceId}/done", HttpMethod.POST),
        DIARY_OCCURRENCE_DEFAULT: new Endpoint("/api/diary/occurrence/{occurrenceId}/default", HttpMethod.POST),
        DIARY_OCCURRENCE_SNOOZED: new Endpoint("/api/diary/occurrence/{occurrenceId}/snoozed", HttpMethod.POST),
        DIARY_SEARCH: new Endpoint("/api/diary/search", HttpMethod.POST),

        //SETTINGS
        GET_USER_SETTINGS: new Endpoint("/api/user/settings/{category}", HttpMethod.GET),
        SET_USER_SETTINGS: new Endpoint("/api/user/settings", HttpMethod.POST),

        //STORAGE
        STORAGE_UPLOAD_FILE: new Endpoint("/api/storage/{storedFileId}", HttpMethod.PUT, null),
        STORAGE_DOWNLOAD_FILE: new Endpoint("/api/storage/{storedFileId}", HttpMethod.GET, null),
        STORAGE_GET_METADATA: new Endpoint("/api/storage/{storedFileId}/metadata", HttpMethod.GET, null),
    }

    this.getEndpoint = function(endpointName, pathVariables, queryParams){
        const ep = endpoints[endpointName] || throwException("IllegalArgument", "Endpoint not found with endpointName " + endpointName);
        return new Endpoint(
            replace(ep.getUrl(), pathVariables, queryParams),
            ep.getMethod(),
            ep.getContentType()
        )
    }

     function replace (path, pathVariables, queryParams){
        let result = path;

        if(pathVariables){
            for(let index in pathVariables){
                if(pathVariables[index] != null){
                    const key = createKey(index);
                    result = result.replace(key, pathVariables[index]);
                }
            }
        }
        if(queryParams){
            result += "?";
            const paramParts = [];
            for (let index in queryParams){
                if(queryParams[index] != null){
                    paramParts.push(index + "=" + queryParams[index]);
                }
            }
            result += paramParts.join("&");
        }

        return result;

        function createKey(index){
            return "{" + index + "}";
        }
    }
}

function Endpoint(u, m, c){
   const url = u;
   const method = m;
   const contentType = c === undefined ? "application/json" : c;

    this.getUrl = function(){
        return url;
   }

    this.getMethod = function(){
        return method;
    }

    this.getContentType = function(){
        return contentType;
    }

    this.toString = toString;

    function toString(){
        return "Endpoint[" + method + " - " + url + " / " + contentType + "]";
    }
}