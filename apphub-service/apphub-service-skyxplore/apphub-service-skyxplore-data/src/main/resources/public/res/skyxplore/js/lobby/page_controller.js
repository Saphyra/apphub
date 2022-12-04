scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/common/js/animation/spinner.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/friends_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/members_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/settings_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/start_game_controller.js");

(function PageController(){
    window.ids = {
        friendsContainer: "friends",
        activeFriendsList: "active-friends-list",
        host: "host",
        membersList: "members-list",
        universeSizeInput: "universe-size-input",
        systemAmountInput: "system-amount-input",
        systemSizeInput: "system-size-input",
        planetSizeInput: "planet-size-input",
        aiPresenceInput: "ai-presence-input",
        startGameButton: "start-game-button",
        noActiveFriends: "no-active-friends",
        gameSettings: "game-settings",
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "chat-send-message",
        SET_READINESS: "set-readiness",
        CHANGE_ALLIANCE: "change-alliance",
        GAME_SETTINGS_CHANGED: "game-settings-changed",
        GAME_CREATION_INITIATED: "game-creation-initiated",
        GAME_LOADED: "game-loaded",
    }

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("WS_CONNECTION_SKYXPLORE_LOBBY"));

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
        localization.loadLocalization("skyxplore", "lobby");

        wsConnection.addHandler(chatController.createChatSendMessageHandler())
            .addHandler(chatController.createCharacterJoinedHandler())
            .addHandler(membersController.createCharacterJoinedHandler())
            .addHandler(chatController.createCharacterLeftHandler())
            .addHandler(membersController.createCharacterLeftHandler())
            .addHandler(membersController.createReadinessHandler())
            .addHandler(membersController.createAllianceChangedHandler())
            .addHandler(settingsController.createGameSettingsChangedHandler())
            .addHandler(startGameController.createGameCreationInitiatedHandler())
            .addHandler(startGameController.createGameLoadedHandler())
            .addHandler(friendsController.createCharacterOnlineHandler())
            .addHandler(friendsController.createCharacterOfflineHandler())
            .connect();

        if(lobbyType == "LOAD_GAME"){
            $("#" + ids.gameSettings).hide();
        }
    });
})();