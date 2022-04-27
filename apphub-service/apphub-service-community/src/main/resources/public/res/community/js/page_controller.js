window.ids = {
    main: "main",
    friendsListContent: "friends-list-content",
    createFriendRequestSearchInput: "create-friend-request-search-input",
    createFriendRequestSearchResultQueryTooShort: "create-friend-request-search-result-query-too-short",
    createFriendRequestSearchResultNoResult: "create-friend-request-search-result-no-result",
    createFriendRequestSearchResult: "create-friend-request-search-result",
    createFriendRequest: "create-friend-request",
    sentFriendRequestsContent: "sent-friend-requests-content",
    receivedFriendRequestsContent: "received-friend-requests-content",
}

scriptLoader.loadScript("/res/community/js/contacts/contacts_controller.js");

(function PageController(){
    window.pageController = new function(){
        this.displayMainPage = function(){
            switchTab("main-page", ids.main);
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "community", fileName: "index"}));
    });
})();