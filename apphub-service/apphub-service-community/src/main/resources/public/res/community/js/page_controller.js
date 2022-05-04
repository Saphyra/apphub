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
    createBlacklist: "create-blacklist",
    createBlacklistSearchInput: "create-blacklist-search-input",
    createBlacklistSearchResultQueryTooShort: "create-blacklist-search-result-query-too-short",
    createBlacklistSearchResultNoResult: "create-blacklist-search-result-no-result",
    createBlacklistSearchResult: "create-blacklist-search-result",
    contactsBlacklistList: "contacts-blacklist-list",
    createGroupNameInput: "create-group-name-input",
    createGroup: "create-group",
    createGroupButton: "create-group-button",
    createGroupInvalidName: "create-group-invalid-name",
    contactsGroupsList: "contacts-groups-list",
    contactsGroupsSearchInput: "contacts-groups-search-input",
    groupDetails: "group-details",
    groupDetailsTitle: "group-details-title",
    groupDetailsInvitationType: "group-details-invitation-type",
    groupDetailsMembers: "group-details-members",
    groupDetailsAddMemberButton: "group-details-add-member-button",
    groupAddMemberSearchInput: "group-add-member-search-input",
    groupAddMemberSearchResult: "group-add-member-search-result",
    groupAddMemberSearchResultQueryTooShort: "group-add-member-search-result-query-too-short",
    groupAddMemberSearchResultNoResult: "group-add-member-search-result-no-result",
    groupAddMember: "group-add-member",
    disbandGroupButton: "disband-group-button",
    leaveGroupButton: "leave-group-button",
    groupDetailsTransferOwnership: "group-details-transfer-ownership",
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