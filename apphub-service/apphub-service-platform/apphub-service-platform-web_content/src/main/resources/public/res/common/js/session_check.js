(function SessionCheck(){
    if(window.SESSION_CHECK_DISABLED == true){
        logService.logToConsole("SessionCheck disabled.");
        return;
    }

    errorHandler.registerHandler(unauthorizedErrorHandler());
    setInterval(checkSession, 10000);
    logService.logToConsole("SessionChecker initialized.");

    function checkSession(){
        const request = new Request(Mapping.getEndpoint("CHECK_SESSION"));
            request.processValidResponse = function(){};
        dao.sendRequestAsync(request);
    }
})();

function unauthorizedErrorHandler(){
    return new ErrorHandler(
        function(request, response){return response.status == ResponseStatus.UNAUTHORIZED},
        function(){window.location.href = Mapping.INDEX_PAGE}
    );
}