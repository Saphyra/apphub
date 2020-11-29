scriptLoader.loadScript("/res/skyxplore/js/lobby/friends_controller.js");

(function PageController(){
    window.ids = {
        friendsContainer: "friends",
        friendsList: "friends-list"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "lobby"}));
    });
})();