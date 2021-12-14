(function LogoutService(){
    events.LOGOUT = "logout";

    eventProcessor.registerProcessor(new EventProcessor(
            function(eventType){return eventType === events.LOGOUT},
            logout,
            false,
            "Logout"
        ));
    
    function logout(){
        const request = new Request(Mapping.getEndpoint("LOGOUT"));
            request.processValidResponse = function(){
                sessionStorage.successMessage = "successful-logout";
                location.href = Mapping.INDEX_PAGE;
            }
            
        dao.sendRequestAsync(request);
    }
})();