(function PageController(){
    scriptLoader.loadScript("/res/user/js/index/login_controller.js");
    scriptLoader.loadScript("/res/user/js/index/registration_controller.js");

    $(document).ready(function(){
        checkLogin();
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "user", fileName: "index"}));
    });

    function checkLogin(){
        const endpoint = Mapping.getEndpoint("CHECK_SESSION");
        const response = dao.sendRequest(endpoint.getMethod(), endpoint.getUrl());
        if(response.status == ResponseStatus.OK){
            window.location.href = Mapping.MODULES_PAGE;
        }
    }
})();