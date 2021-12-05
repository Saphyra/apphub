scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
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
        noResult: "no-result"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "ban"}));
    });
})();