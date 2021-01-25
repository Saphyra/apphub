(function PageController(){
    scriptLoader.loadScript("/res/user/js/index/login_controller.js");
    scriptLoader.loadScript("/res/user/js/index/registration_controller.js");

    $(document).ready(function(){
        checkLogin();
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "user", fileName: "index"}));
    });

    function checkLogin(){
        const request = new Request(Mapping.getEndpoint("CHECK_SESSION"));
            request.processValidResponse = function(){
                window.location.href = Mapping.MODULES_PAGE;
            }
            request.processInvalidResponse = function(){
            }
        dao.sendRequest(request);
    }
})();