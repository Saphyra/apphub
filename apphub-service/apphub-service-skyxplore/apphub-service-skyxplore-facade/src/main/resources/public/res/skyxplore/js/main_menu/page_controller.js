scriptLoader.loadScript("/res/skyxplore/js/main_menu/friend_controller.js");

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
        friendsContainer: "friends-container"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "main_menu"}));
    });

    window.pageController = new function(){
        this.openMainMenu = function(){
            switchTab("main-page", "main-menu");
        }

        this.openNewGameWindow = function(){
            switchTab("main-page", "new-game");
        }

        this.openLoadGameWindow = function(){
            notificationService.showSuccess("Opening load game window");
        }
    }
})();