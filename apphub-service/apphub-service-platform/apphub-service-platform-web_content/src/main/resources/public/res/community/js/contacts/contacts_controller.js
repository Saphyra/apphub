scriptLoader.loadScript("/res/community/js/contacts/blacklist/blacklist_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/friends/friends_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/groups/groups_controller.js");

(function ContactsController(){
    pageLoader.addLoader(openFriendsTab, "Switching to friends tab.");

    window.contactsController = new function(){
        this.openFriendsTab = openFriendsTab;
        this.openGroupsTab = openGroupsTab;
        this.openBlacklistTab = openBlacklistTab;
    }

    function openFriendsTab(){
        openTab("friends", friendsController.load);
    }

    function openGroupsTab(){
        openTab("groups", groupsController.load);
    }

    function openBlacklistTab(){
        openTab("blacklist", blacklistController.load);
    }

    function openTab(id, callback){
        $(".contacts-tab-switch-button").removeClass("selected-tab-button");
        $("#" + id + "-button").addClass("selected-tab-button");

        $(".contacts-panel").hide(0);
        $("#contacts-" + id).show(0);

        callback();
    }
})();