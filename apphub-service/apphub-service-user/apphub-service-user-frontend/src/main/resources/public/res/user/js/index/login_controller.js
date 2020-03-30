(function Login(){
    window.events.LOGIN_ATTEMPT = "login_attempt";

    $(document).ready(function(){
        $(".login-input").on("keyup", function(e){
            if(e.which == 13){
                eventProcessor.processEvent(new Event(events.LOGIN_ATTEMPT));
            }
        });
    })

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOGIN_ATTEMPT},
        login
    ));
    
    function login(event){
        const credentials = new Credentials(event.getPayload());
        $("#login-password").val("");
        
        if(!credentials.isValid()){
            notificationService.showError(Localization.getAdditionalContent("empty-credentials"));
            return;
        }
        
        const request = new Request(Mapping.getEndpoint("LOGIN"), credentials.stringify());
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(loginResponse){
                setCookie("access-token", loginResponse.accessTokenId, loginResponse.expirationDays);
                location.href = Mapping.MODULES_PAGE;
            };
            request.handleLogout = false;
        dao.sendRequestAsync(request);
    }
    
    function Credentials(payload){
        const email = payload ? payload.email : $("#login-email").val();
        const password = payload ? payload.password : $("#login-password").val();
        
        this.isValid = function(){
            return email !== "" && password !== "";
        }
        
        this.stringify = function(){
            return {email: email, password: password};
        }
    }
})();