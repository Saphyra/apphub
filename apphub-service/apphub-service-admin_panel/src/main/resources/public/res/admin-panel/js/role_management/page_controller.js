scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/admin-panel/js/role_management/role_controller.js");

(function PageController(){
    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "role_management"}));
    });
})();