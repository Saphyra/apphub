(function PageController(){
    scriptLoader.loadScript("/res/admin-panel/js/disabled_role_management/disabled_role_controller.js");

    window.ids = {
        roleContainer: "roles",
        confirmationDialogId: "confirm-operation",
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "disable_role_management"}));
    });
})();