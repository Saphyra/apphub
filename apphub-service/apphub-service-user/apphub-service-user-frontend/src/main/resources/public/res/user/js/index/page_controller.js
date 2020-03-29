(function PageController(){
    scriptLoader.loadScript("/res/user/js/index/login_controller.js");
    scriptLoader.loadScript("/res/user/js/index/registration_controller.js");

    $(document).ready(function(){
        init();
    });
    
    function init(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "user", fileName: "index"}));
    }
})();