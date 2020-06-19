(function PageController(){
    scriptLoader.loadScript("/res/admin-panel/js/role_management/role_controller.js");

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "role_management"}));
    });
})();