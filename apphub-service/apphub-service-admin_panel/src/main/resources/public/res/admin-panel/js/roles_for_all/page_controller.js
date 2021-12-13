scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/roles_for_all/roles_controller.js");

(function PageController(){
    window.ids = {
        roles: "roles"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "roles_for_all"}));
    });
})();