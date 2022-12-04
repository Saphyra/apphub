scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/disabled_role_management/disabled_role_controller.js");

(function PageController(){
    window.ids = {
        roleContainer: "roles",
        confirmationDialogId: "confirm-operation",
    }

    $(document).ready(function(){
        localization.loadLocalization("admin_panel", "disabled_role_management");
    });
})();