scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/friends_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/lobby/members_controller.js");

(function PageController(){
    window.ids = {
        friendsContainer: "friends",
        friendsList: "friends-list",
        host: "host",
        membersList: "members-list"
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "chat-send-message"
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
            .connect();
    });
})();