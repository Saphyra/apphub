(function RegistrationController(){
    scriptLoader.loadScript("/res/common/js/validation_util.js");

    events.REGISTER_ATTEMPT = "register_attempt";
    events.VALIDATION_ATTEMPT = "validation_attempt";

    const INVALID_USERNAME = "#invalid-username";
    const INVALID_PASSWORD = "#invalid-password";
    const INVALID_CONFIRM_PASSWORD = "#invalid-confirm-password";

    let registrationAllowed = false;
    let validationTimeout = null;

    $(document).ready(function(){
        $(".reg-input").on("keyup", function(e){
            if(e.which == 13){
                eventProcessor.processEvent(new Event(events.REGISTER_ATTEMPT));
            }else{
                eventProcessor.processEvent(new Event(events.VALIDATION_ATTEMPT));
            }
        });
        $(".reg-input").on("focusin", function(){
            eventProcessor.processEvent(new Event(events.VALIDATION_ATTEMPT));
        });
    });

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.VALIDATION_ATTEMPT},
        function(){
            blockRegistration();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }
            validationTimeout = setTimeout(validateInputs, getValidationTimeout());
        }
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.REGISTER_ATTEMPT},
        function(){
            if(!registrationAllowed){
                return;
            }

            const user = {
                userName: getUserName(),
                password: getPassword()
            }

            const request = new Request(HttpMethod.PUT, Mapping.REGISTER, user);
                request.processValidResponse = function(){
                    sessionStorage.successMessage = "registration-successful";
                    eventProcessor.processEvent(new Event(events.LOGIN_ATTEMPT, user));
                }
            dao.sendRequestAsync(request);
        }
    ));

    function validateInputs(){
        const userName = getUserName();
        const password = getPassword();
        const confirmPassword = getConfirmPassword();

        const validationResults = [];
            validationResults.push(validatePassword(password));
            validationResults.push(validateConfirmPassword(password, confirmPassword));
            validationResults.push(validateUserName(userName));
        processValidationResults(validationResults);

        function processValidationResults(validationResults){
            registrationAllowed = new Stream(validationResults)
                .peek(function(validationResult){validationResult.process()})
                .allMatch(function(validationResult){return validationResult.isValid});

            setRegistrationButtonState();
        }

        function validatePassword(password){
            if(password.length < 6){
                return {
                    isValid: false,
                    process: createErrorProcess(INVALID_PASSWORD, "password-too-short")
                };
            }else if(password.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(INVALID_PASSWORD, "password-too-long")
                };
            }else{
                return{
                    isValid: true,
                    process: createSuccessProcess(INVALID_PASSWORD)
                };
            }
        }

        function validateConfirmPassword(password, confirmPassword){
            if(password !== confirmPassword){
                return {
                    isValid: false,
                    process: createErrorProcess(INVALID_CONFIRM_PASSWORD, "incorrect-confirm-password")
                };
            }else{
                return{
                    isValid: true,
                    process: createSuccessProcess(INVALID_CONFIRM_PASSWORD)
                };
            }
        }

        function validateUserName(userName){
            if(userName.length < 3){
                return {
                    isValid: false,
                    process: createErrorProcess(INVALID_USERNAME, "username-too-short")
                };
            }else if(userName.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(INVALID_USERNAME, "username-too-long")
                };
            }else{
                return {
                    isValid: true,
                    process: createSuccessProcess(INVALID_USERNAME)
                };
            }
        }

        function getConfirmPassword(){
            return $("#reg-confirm-password").val();
        }
    }

    function getUserName(){
        return $("#reg-username").val();
    }

    function getPassword(){
        return $("#reg-password").val();
    }

    function blockRegistration(){
        registrationAllowed = false;
        setRegistrationButtonState();
    }

    function setRegistrationButtonState(){
        document.getElementById("registration-button").disabled = !registrationAllowed;
    }
})();