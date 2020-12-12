scriptLoader.loadScript("/res/skyxplore/js/main_menu/friend_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/main_menu/invitation_controller.js");
scriptLoader.loadScript("/res/common/js/web_socket.js");

(function PageController(){
    window.ids = {
        searchFriendInput: "new-friend-name",
        friendSearchResultWrapper: "new-friend-search-result-wrapper",
        queryStringTooShort: "query-string-too-short",
        characterNotFound: "no-character-found",
        friendSearchResult: "new-friend-search-result",
        sentFriendRequestsContainer: "outgoing-friend-request-list",
        incomingFriendRequestsContainer: "incoming-friend-request-list",
        friendListContainer: "friend-list",
        friendsContainer: "friends-container",
        invitationContainer: "invitations",
    }

    const webSocketConnection = new WebSocketConnection(Mapping.getEndpoint("CONNECTION_SKYXPLORE_MAIN_MENU"));

    window.pageController = new function(){
        this.newGame = function(){
            const request = new Request(Mapping.getEndpoint("SKYXPLORE_CREATE_LOBBY"));
                request.processValidResponse = function(){
                    window.location.href='/web/skyxplore/lobby'
                }
            dao.sendRequestAsync(request);
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "main_menu"}));
        webSocketConnection.addHandler(invitationController.createHandler())
            .connect();
    });
})();