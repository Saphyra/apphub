window.Mapping = new function(){
    scriptLoader.loadScript("/res/common/js/dao/http_method.js");

    this.INDEX_PAGE = "/web";
    this.MODULES_PAGE = "/web/modules";
    this.SKYXPLORE_PAGE = "/web/skyxplore";
    this.SKYXPLORE_LOBBY_PAGE = "/web/skyxplore/lobby";
    this.SKYXPLORE_GAME_PAGE = "/web/skyxplore/game";

    const endpoints = {
        CONNECTION_SKYXPLORE_MAIN_MENU: new Endpoint("/api/message-sender/skyxplore/main-menu", HttpMethod.GET),
        CONNECTION_SKYXPLORE_LOBBY: new Endpoint("/api/message-sender/skyxplore/lobby", HttpMethod.GET),
        CONNECTION_SKYXPLORE_GAME: new Endpoint("/api/message-sender/skyxplore/game", HttpMethod.GET),

        ADD_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.PUT),
        ADMIN_PANEL_MENU: new Endpoint("/res/admin-panel/json/index_menu.json", HttpMethod.GET),
        AVAILABLE_ROLES: new Endpoint("/res/admin-panel/json/available_roles.json", HttpMethod.GET),
        CHANGE_EMAIL: new Endpoint("/api/user/account/email", HttpMethod.POST),
        CHANGE_LANGUAGE: new Endpoint("/api/user/account/language", HttpMethod.POST),
        CHANGE_PASSWORD: new Endpoint("/api/user/account/password", HttpMethod.POST),
        CHANGE_USERNAME: new Endpoint("/api/user/account/username", HttpMethod.POST),
        CHECK_SESSION: new Endpoint("/api/user/authentication/session", HttpMethod.GET),
        CLONE_NOTEBOOK_LIST_ITEM: new Endpoint("/api/notebook/{listItemId}/clone", HttpMethod.POST),
        CONVERT_NOTEBOOK_TABLE_TO_CHECKLIST_TABLE: new Endpoint("/api/notebook/table/{listItemId}/convert-to-checklist-table", HttpMethod.POST),
        CREATE_NOTEBOOK_CATEGORY: new Endpoint("/api/notebook/category", HttpMethod.PUT),
        CREATE_NOTEBOOK_LINK: new Endpoint("/api/notebook/link", HttpMethod.PUT),
        CREATE_NOTEBOOK_TEXT: new Endpoint("/api/notebook/text", HttpMethod.PUT),
        CREATE_NOTEBOOK_TABLE: new Endpoint("/api/notebook/table", HttpMethod.PUT),
        CREATE_NOTEBOOK_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table", HttpMethod.PUT),
        CREATE_NOTEBOOK_CHECKLIST: new Endpoint("/api/notebook/checklist", HttpMethod.PUT),
        DELETE_ACCOUNT: new Endpoint("/api/user/account", HttpMethod.DELETE),
        DELETE_NOTEBOOK_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}", HttpMethod.DELETE),
        EDIT_NOTEBOOK_CHECKLIST_ITEM: new Endpoint("/api/notebook/checklist/{listItemId}", HttpMethod.POST),
        EDIT_NOTEBOOK_LIST_ITEM: new Endpoint("/api/notebook/item/{listItemId}", HttpMethod.POST),
        EDIT_NOTEBOOK_TEXT: new Endpoint("/api/notebook/text/{listItemId}", HttpMethod.POST),
        EDIT_NOTEBOOK_TABLE: new Endpoint("/api/notebook/table/{listItemId}", HttpMethod.POST),
        EDIT_NOTEBOOK_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}", HttpMethod.POST),
        GET_NOTEBOOK_CHECKLIST_ITEM: new Endpoint("/api/notebook/checklist/{listItemId}", HttpMethod.GET),
        GET_CHILDREN_OF_NOTEBOOK_CATEGORY: new Endpoint("/api/notebook/category/children", HttpMethod.GET),
        GET_LANGUAGES: new Endpoint("/api/user/data/languages", HttpMethod.GET),
        GET_MODULES: new Endpoint("/api/modules", HttpMethod.GET),
        GET_NOTEBOOK_CATEGORIES: new Endpoint("/api/notebook/category", HttpMethod.GET),
        GET_NOTEBOOK_TEXT: new Endpoint("/api/notebook/text/{listItemId}", HttpMethod.GET),
        GET_NOTEBOOK_TABLE: new Endpoint("/api/notebook/table/{listItemId}", HttpMethod.GET),
        GET_NOTEBOOK_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}", HttpMethod.GET),
        GET_USER_ROLES: new Endpoint("/api/user/data/roles", HttpMethod.POST),
        LOGIN: new Endpoint("/api/user/authentication/login", HttpMethod.POST),
        LOGOUT: new Endpoint("/api/user/authentication/logout", HttpMethod.POST),
        MARK_AS_FAVORITE: new Endpoint("/api/modules/{module}/favorite", HttpMethod.POST),
        REGISTER: new Endpoint("/api/user", HttpMethod.POST),
        REMOVE_ROLE: new Endpoint("/api/user/data/roles", HttpMethod.DELETE),
        UPDATE_NOTEBOOK_CHECKLIST_ITEM_STATUS: new Endpoint("/api/notebook/checklist/item/{checklistItemId}/status", HttpMethod.POST),
        UPDATE_NOTEBOOK_CHECKLIST_TABLE_ROW_STATUS: new Endpoint("/api/notebook/checklist-table/{listItemId}/{rowIndex}", HttpMethod.POST),
        NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST: new Endpoint("/api/notebook/checklist/{listItemId}/checked", HttpMethod.DELETE),
        NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE: new Endpoint("/api/notebook/checklist-table/{listItemId}/checked", HttpMethod.DELETE),

        SKYXPLORE_GET_CHARACTER: new Endpoint("/api/skyxplore/character", HttpMethod.GET),
        SKYXPLORE_CREATE_OR_UPDATE_CHARACTER: new Endpoint("/api/skyxplore/character", HttpMethod.POST),
        SKYXPLORE_SEARCH_FOR_FRIENDS: new Endpoint("/api/skyxplore/friend/candidate", HttpMethod.POST),
        SKYXPLORE_ADD_FRIEND: new Endpoint("/api/skyxplore/friend/request", HttpMethod.PUT),
        SKYXPLORE_GET_SENT_FRIEND_REQUEST: new Endpoint("/api/skyxplore/friend/request/sent", HttpMethod.GET),
        SKYXPLORE_GET_INCOMING_FRIEND_REQUEST: new Endpoint("/api/skyxplore/friend/request/incoming", HttpMethod.GET),
        SKYXPLORE_CANCEL_FRIEND_REQUEST: new Endpoint("/api/skyxplore/friend/request/{friendRequestId}", HttpMethod.DELETE),
        SKYXPLORE_ACCEPT_FRIEND_REQUEST: new Endpoint("/api/skyxplore/friend/request/{friendRequestId}", HttpMethod.POST),
        SKYXPLORE_GET_FRIENDS: new Endpoint("/api/skyxplore/friend", HttpMethod.GET),
        SKYXPLORE_REMOVE_FRIEND: new Endpoint("/api/skyxplore/friend/{friendshipId}", HttpMethod.DELETE),
        SKYXPLORE_GET_ACTIVE_FRIENDS: new Endpoint("/api/skyxplore/friend/active", HttpMethod.GET),
        SKYXPLORE_EXIT_FROM_LOBBY: new Endpoint("/api/skyxplore/lobby", HttpMethod.DELETE),
        SKYXPLORE_INVITE_TO_LOBBY: new Endpoint("/api/skyxplore/lobby/invite/{friendId}", HttpMethod.POST),
        SKYXPLORE_LOBBY_GET_MEMBERS: new Endpoint("/api/skyxplore/lobby/members", HttpMethod.GET),
        SKYXPLORE_CREATE_LOBBY: new Endpoint("/api/skyxplore/lobby", HttpMethod.PUT),
        SKYXPLORE_LOBBY_ACCEPT_INVITATION: new Endpoint("/api/skyxplore/lobby/join/{invitorId}", HttpMethod.POST),
        SKYXPLORE_LOBBY_GET_GAME_SETTINGS: new Endpoint("/api/skyxplore/lobby/settings", HttpMethod.GET),
        SKYXPLORE_START_GAME: new Endpoint("/api/skyxplore/lobby/start", HttpMethod.POST),
        SKYXPLORE_GET_UNIVERSE: new Endpoint("/api/skyxplore/game/universe", HttpMethod.GET),
        SKYXPLORE_GAME_GET_PLAYERS: new Endpoint("/api/skyxplore/game/player", HttpMethod.GET),
        SKYXPLORE_GAME_CREATE_CHAT_ROOM: new Endpoint("/api/skyxplore/game/chat/room", HttpMethod.PUT),
        SKYXPLORE_GAME_LEAVE_ROOM: new Endpoint("/api/skyxplore/game/chat/room/{roomId}", HttpMethod.DELETE),
        SKYXPLORE_GET_SOLAR_SYSTEM: new Endpoint("/api/skyxplore/game/solar-system/{solarSystemId}", HttpMethod.GET),
        SKYXPLORE_GET_PLANET_SURFACE: new Endpoint("/api/skyxplore/game/planet/{planetId}/surface", HttpMethod.GET),
        SKYXPLORE_GET_PLANET_STORAGE: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage", HttpMethod.GET),
        SKYXPLORE_GET_ITEM_DATA: new Endpoint("/api/skyxplore/data/{dataId}", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_POPULATION_OVERVIEW: new Endpoint("/api/skyxplore/game/planet/{planetId}/population", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_BUILDING_OVERVIEW: new Endpoint("/api/skyxplore/game/planet/{planetId}/building", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_PRIORITIES: new Endpoint("/api/skyxplore/game/planet/{planetId}/priority", HttpMethod.GET),
        SKYXPLORE_PLANET_GET_STORAGE_SETTINGS: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings", HttpMethod.GET),
        SKYXPLORE_PLANET_CREATE_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings", HttpMethod.POST),
        SKYXPLORE_PLANET_DELETE_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings/{storageSettingId}", HttpMethod.DELETE),
        SKYXPLORE_PLANET_EDIT_STORAGE_SETTING: new Endpoint("/api/skyxplore/game/planet/{planetId}/storage-settings/{storageSettingId}", HttpMethod.POST),
        SKYXPLORE_PLANET_GET_POPULATION: new Endpoint("/api/skyxplore/game/planet/{planetId}/citizen", HttpMethod.GET),
        SKYXPLORE_PLANET_RENAME_CITIZEN: new Endpoint("/api/skyxplore/game/planet/{planetId}/citizen/{citizenId}/rename", HttpMethod.POST),
        SKYXPLORE_PLANET_UPDATE_PRIORITY: new Endpoint("/api/skyxplore/game/planet/{planetId}/priority/{priorityType}", HttpMethod.POST),
    }

    this.getEndpoint = function(endpointName, pathVariables, queryParams){
        const ep = endpoints[endpointName] || throwException("IllegalArgument", "Endpoint not found with endpointName " + endpointName);
        return new Endpoint(
            replace(ep.getUrl(), pathVariables, queryParams),
            ep.getMethod()
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

function Endpoint(u, m){
   const url = u;
   const method = m;

    this.getUrl = function(){
        return url;
   }

    this.getMethod = function(){
        return method;
    }
}