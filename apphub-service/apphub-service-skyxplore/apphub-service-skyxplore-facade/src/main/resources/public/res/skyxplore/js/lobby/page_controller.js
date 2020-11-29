scriptLoader.loadScript("/res/skyxplore/js/lobby/friends_controller.js");

(function PageController(){
    window.ids = {
        friendsContainer: "friends",
        friendsList: "friends-list"
    }

    window.pageController = new function(){
        this.exitLobby = function(){
            const request = new Request(Mapping.getEndpoint("SKYXPLORE_EXIT_FROM_LOBBY"));
                request.processValidResponse = function(){
                    window.location = "/web/skyxplore";
                }
            dao.sendRequestAsync(request);
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "lobby"}));
    });
})();