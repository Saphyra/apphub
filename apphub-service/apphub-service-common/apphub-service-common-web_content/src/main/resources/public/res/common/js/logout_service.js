(function LogoutService(){
    events.LOGOUT = "logout";

    eventProcessor.registerProcessor(new EventProcessor(
            function(eventType){return eventType === events.LOGOUT},
            logout
        ));
    
    function logout(){
        const request = new Request(HttpMethod.POST, Mapping.LOGOUT);
            request.processValidResponse = function(){
                sessionStorage.successMessage = "successful-logout";
                location.href = Mapping.INDEX_PAGE;
            }
            
        dao.sendRequestAsync(request);
    }
})();