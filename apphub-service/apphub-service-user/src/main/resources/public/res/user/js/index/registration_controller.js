(function RegistrationController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");

    const INVALID_USERNAME = "#invalid-username";
    const INVALID_EMAIL = "#invalid-email";
    const INVALID_PASSWORD = "#invalid-password";
    const INVALID_CONFIRM_PASSWORD = "#invalid-confirm-password";

    let registrationAllowed = false;
    let validationTimeout = null;

    pageLoader.addLoader(function(){
        $(".reg-input").on("keyup", function(e){
            if(e.which == 13){
                registerAttempt();
            }else{
                validationAttempt();
            }
        });
        $(".reg-input").on("focusin", function(){
            validationAttempt();
        });
    }, "Registration add event listeners");

    window.registrationController = new function(){
        this.registerAttempt = registerAttempt;
    }

    function validationAttempt(){
        blockRegistration();

        if(validationTimeout){
            clearTimeout(validationTimeout);
        }
        validationTimeout = setTimeout(validateInputs, getValidationTimeout());
    }

    function registerAttempt(){
        if(!registrationAllowed){
            return;
        }

        const user = {
            username: getUsername(),
            email: getEmail(),
            password: getPassword()
        }

        $(".reg-input").val("");

        const request = new Request(Mapping.getEndpoint("ACCOUNT_REGISTER"), user);
            request.processValidResponse = function(){
                loginController.login(user);
            }
        dao.sendRequestAsync(request);
    }

    function validateInputs(){
        const username = getUsername();
        const email = getEmail();
        const password = getPassword();
        const confirmPassword = getConfirmPassword();

        const validationResults = validationService.bulkValidation([
            function(){return validationService.validatePassword(INVALID_PASSWORD, password)},
            function(){return validationService.validateConfirmPassword(INVALID_CONFIRM_PASSWORD, password, confirmPassword)},
            function(){return validationService.validateUsername(INVALID_USERNAME, username)},
            function(){return validationService.validateEmail(INVALID_EMAIL, email)}
        ]);
        processValidationResults(validationResults);

        function processValidationResults(validationResults){
            registrationAllowed = new Stream(validationResults)
                .peek(function(validationResult){validationResult.process()})
                .allMatch(function(validationResult){return validationResult.isValid});

            setRegistrationButtonState();
        }

        function getConfirmPassword(){
            return $("#reg-confirm-password").val();
        }
    }

    function getUsername(){
        return $("#reg-username").val();
    }
    
    function getEmail(){
            return $("#reg-email").val();
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