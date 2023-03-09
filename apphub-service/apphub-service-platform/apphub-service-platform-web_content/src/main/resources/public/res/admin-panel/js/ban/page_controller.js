scriptLoader.loadScript("/res/common/js/date.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/ban/search_controller.js");
scriptLoader.loadScript("/res/admin-panel/js/ban/ban_controller.js");

(function PageController(){
    window.ids = {
        search: "search",
        searchResult: "search-result",
        searchResultContent: "search-result-content",
        searchTextTooShort: "search-text-too-short",
        bannableRoles: "bannable-roles",
        userDetailsUserId: "user-details-user-id",
        userDetailsUsername: "user-details-username",
        userDetailsEmail: "user-details-email",
        permanent: "permanent",
        duration: "duration",
        chronoUnit: "chrono-unit",
        reason: "reason",
        password: "password",
        currentBans: "current-bans",
        noCurrentBans: "no-current-bans",
        userDetails: "user-details",
        userList: "user-list",
        noResult: "no-result",
        markedForDeletion: "user-marked-for-deletion",
        markedForDeletionAt: "user-marked-for-deletion-at",
        markedForDeletionAtContainer: "user-marked-for-deletion-at-container",
        unmarkForDeletionButton: "unmark-for-deletion-button",
        deleteUserInputContainer: "delete-user-input-container",
        deleteTheUserAtDate: "delete-the-user-at-date",
        deleteTheUserAtTime: "delete-the-user-at-time",
        confirmationDialogId: "user-deletion-confirmation-dialog",
    }

    $(document).ready(function(){
        localization.loadLocalization("admin-panel", "ban");
    });
})();