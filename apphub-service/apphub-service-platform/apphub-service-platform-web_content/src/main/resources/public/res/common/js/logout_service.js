(function LogoutService(){
    window.logoutService = new function(){
        this.logout = logout;
    }

    function logout(){
        const request = new Request(Mapping.getEndpoint("LOGOUT"));
            request.processValidResponse = function(){
                sessionStorage.successMessage = "successful-logout";
                location.href = Mapping.INDEX_PAGE;
            }
        dao.sendRequestAsync(request);
    }
})();