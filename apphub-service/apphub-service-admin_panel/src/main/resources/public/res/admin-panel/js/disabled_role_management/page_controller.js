scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/disabled_role_management/disabled_role_controller.js");

(function PageController(){
    window.ids = {
        roleContainer: "roles",
        confirmationDialogId: "confirm-operation",
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "disable_role_management"}));
    });
})();