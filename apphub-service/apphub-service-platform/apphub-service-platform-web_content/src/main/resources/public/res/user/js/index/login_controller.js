(function Login(){
    pageLoader.addLoader(function(){
        $(".login-input").on("keyup", function(e){
            if(e.which == 13){
                login();
            }
        });
    }, "Login add event listeners");

    window.loginController = new function(){
        this.login = login;
    }
    
    function login(userData){
        const credentials = userData ? new Credentials(userData) : new Credentials();
        $("#login-password").val("");
        
        if(!credentials.isValid()){
            notificationService.showError(localization.getAdditionalContent("empty-credentials"));
            return;
        }
        
        const request = new Request(Mapping.getEndpoint("LOGIN"), credentials.stringify());
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(loginResponse){
                setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
                location.href = getQueryParam("redirect") || Mapping.MODULES_PAGE;
            };
            request.handleLogout = false;
        dao.sendRequestAsync(request);
    }
    
    function Credentials(payload){
        const email = payload ? payload.email : $("#login-email").val();
        const password = payload ? payload.password : $("#login-password").val();
        const rememberMe = payload ? false : document.getElementById("remember-me").checked

        this.isValid = function(){
            return email !== "" && password !== "";
        }
        
        this.stringify = function(){
            return {email: email, password: password, rememberMe: rememberMe};
        }
    }
})();