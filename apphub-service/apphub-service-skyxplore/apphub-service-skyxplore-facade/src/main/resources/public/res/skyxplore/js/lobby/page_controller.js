scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/friends_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/members_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/settings_controller.js");

(function PageController(){
    window.ids = {
        friendsContainer: "friends",
        friendsList: "friends-list",
        host: "host",
        membersList: "members-list",
        universeSizeInput: "universe-size-input",
        systemSizeInput: "system-size-input",
        planetSizeInput: "planet-size-input",
        aiPresenceInput: "ai-presence-input",
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "chat-send-message",
        SET_READINESS: "set-readiness",
        CHANGE_ALLIANCE: "change-alliance",
        GAME_SETTINGS_CHANGED: "game-settings-changed",
    }

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("CONNECTION_SKYXPLORE_LOBBY"));

    window.pageController = new function(){
        this.webSocketConnection = wsConnection;

        this.exitLobby = function(){
            const request = new Request(Mapping.getEndpoint("SKYXPLORE_EXIT_FROM_LOBBY"));
                request.processValidResponse = function(){
                    wsConnection.close();
                    window.location = "/web/skyxplore";
                }
            dao.sendRequestAsync(request);
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "lobby"}));
        wsConnection.addHandler(chatController.createChatSendMessageHandler())
            .addHandler(chatController.createCharacterJoinedHandler())
            .addHandler(membersController.createCharacterJoinedHandler())
            .addHandler(chatController.createCharacterLeftHandler())
            .addHandler(membersController.createReadinessHandler())
            .addHandler(membersController.createAllianceChangedHandler())
            .addHandler(settingsController.createGameSettingsChangedHandler())
            //TODO Handle member exit
            .connect();
    });
})();