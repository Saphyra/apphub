scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/skyxplore/js/game/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map/map_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/solar_system/solar_system_controller.js");

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
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "skyxplore-game-chat-send-message",
        USER_JOINED: "skyxplore-game-user-joined",
        USER_LEFT: "skyxplore-game-user-left",
        CHAT_ROOM_CREATED: "skyxplore-game-chat-room-created",
    }

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("CONNECTION_SKYXPLORE_GAME"));

    window.pageController = new function(){
        this.webSocketConnection = wsConnection;

        this.showMap = function(){
            switchTab("main-tab", "map");
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