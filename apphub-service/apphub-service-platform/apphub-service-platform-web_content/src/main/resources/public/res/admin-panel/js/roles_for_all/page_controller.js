scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/roles_for_all/roles_controller.js");

(function PageController(){
    window.ids = {
        roles: "roles"
    }

    $(document).ready(function(){
        localization.loadLocalization("admin-panel", "roles_for_all");
    });
})();