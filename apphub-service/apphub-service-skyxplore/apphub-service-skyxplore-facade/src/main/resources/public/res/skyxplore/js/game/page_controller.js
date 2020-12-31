scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/common/js/cache.js");
scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/skyxplore/js/game/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map/map_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/solar_system/solar_system_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_controller.js");

(function PageController(){
    window.ids = {
        mapSvgContainer: "map-svg-container",
        mapContainer: "map-container",
        chatButton: "chat-button",
        chatContainer: "chat",
        chatMessageInput: "send-message-input",
        generalChatButton: "general-chat-button",
        allianceChatButton: "alliance-chat-button",
        createChatRoomTitleInput: "create-chat-room-title-input",
        createChatRoomNoPlayers: "create-chat-room-no-players",
        createChatRoomPlayerList: "create-chat-room-players",
        createChatRoomContainer: "create-chat-room-container",
        chatRooms: "chat-rooms",
        chatMessageContainers: "chat-message-containers",
        solarSystemSvgContainer: "solar-system-svg-container",
        solarSystemContainer: "solar-system-container",
        solarSystem: "solar-system",
        solarSystemName: "solar-system-name",
        closePlanetButton: "close-planet-button",
        planet: "planet",
        planetSurfaceContainer: "planet-surface-container",
        planetMiddleBar: "planet-middle-bar",
        toggleEnergyDetailsButton: "toggle-energy-details-button",
        toggleLiquidDetailsButton: "toggle-liquid-details-button",
        toggleBulkDetailsButton: "toggle-bulk-details-button",
        planetStorageEnergyDetailsContainer: "planet-storage-energy-details-container",
        planetStorageLiquidDetailsContainer: "planet-storage-liquid-details-container",
        planetStorageBulkDetailsContainer: "planet-storage-bulk-details-container",
        planetOpenPopulationOverviewButton: "planet-open-population-overview-button",
        planetPopulationOverviewProgressBarBackground: "planet-population-overview-progress-bar-background",
        planetPopulationOverviewActual: "planet-population-overview-actual",
        planetPopulationOverviewCapacity: "planet-population-overview-capacity",
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "skyxplore-game-chat-send-message",
        USER_JOINED: "skyxplore-game-user-joined",
        USER_LEFT: "skyxplore-game-user-left",
        CHAT_ROOM_CREATED: "skyxplore-game-chat-room-created",
    }

    window.itemData = new Cache(itemDataLoader);
    window.itemDataNameLocalization = new CustomLocalization("skyxplore", "items");

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("CONNECTION_SKYXPLORE_GAME"));

    window.pageController = new function(){
        this.webSocketConnection = wsConnection;

        this.showMap = function(){
            switchTab("main-tab", "map");
        }
    }

    function itemDataLoader(itemId){
        const endpoint = Mapping.getEndpoint("SKYXPLORE_GET_ITEM_DATA", {dataId, itemId});
        const response = dao.sendRequest(endpoint.getMethod(), endpoint.getUrl());

        if(response.status == ResponseStatus.OK){
            const result = JSON.parse(response.body);
            logService.logToConsole("Item loaded with id " + itemId, result);
            return result;
        }else{
            new ErrorHandlerRegistry()
                .handleError(null, response);
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "game"}));
        wsConnection.addHandler(chatController.createChatSendMessageHandler())
            .addHandler(chatController.createUserJoinedHandler())
            .addHandler(chatController.createUserLeftHandler())
            .addHandler(chatController.createChatRoomCreatedHandler())
            .connect();
        //document.addEventListener('contextmenu', event => event.preventDefault()); //TODO restore when development is finished
    });
})();