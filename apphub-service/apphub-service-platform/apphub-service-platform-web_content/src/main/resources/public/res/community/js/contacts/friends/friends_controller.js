scriptLoader.loadScript("/res/community/js/contacts/friends/create_friend_request_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/friends/friend_request_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/friends/friendship_controller.js");

(function FriendsController(){
    window.friendsController = new function(){
        this.load = load;
    }

    function load(){
        friendshipController.load();
        friendRequestController.loadReceivedFriendRequests();
        friendRequestController.loadSentFriendRequests();
    }
})();