(function ChangeUsernameController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");
    
    const INVALID_USERNAME = "#ch-username-invalid-new-username";
    const INVALID_PASSWORD = "#ch-username-invalid-password";

    let submissionAllowed = false;
    let validationTimeout = null;

    pageLoader.addLoader(function(){
        $(".change-username-input").on("keyup", function(e){
            if(e.which == 13){
                changeUsernameAttempt();
            }else{
                changeUsernameValidationAttempt();
            }
        });
        $(".change-username-input").on("focusin", function(){
            changeUsernameValidationAttempt();
        });
    }, "ChangeUsername add event listeners");

    window.changeUsernameController = new function(){
        this.changeUsernameAttempt = changeUsernameAttempt;
    }

    function changeUsernameValidationAttempt(){
        blockSubmission();

        if(validationTimeout){
            clearTimeout(validationTimeout);
        }
        validationTimeout = setTimeout(validateInputs, getValidationTimeout());
    }

    function changeUsernameAttempt(){
        if(!submissionAllowed){
            return;
        }

        const payload = {
            username: getUsername(),
            password: getPassword()
        }

        $("#ch-username-password-input").val("");

        const request = new Request(Mapping.getEndpoint("ACCOUNT_CHANGE_USERNAME"), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("username-changed"));
            }
        dao.sendRequestAsync(request);
        blockSubmission();
    }

    function validateInputs(){
        const username = getUsername();
        const password = getPassword();

        const validationResults = validationService.bulkValidation([
            function(){return validationService.validateUsername(INVALID_USERNAME, username)},
            function(){return validationService.validatePasswordFilled(INVALID_PASSWORD, password)}
        ]);

        processValidationResults(validationResults);

        function processValidationResults(validationResults){
            submissionAllowed = new Stream(validationResults)
                .peek(function(validationResult){validationResult.process()})
                .allMatch(function(validationResult){return validationResult.isValid});

            setButtonState();
        }
    }

    function getUsername(){
        return $("#ch-username-new-username-input").val();
    }

    function getPassword(){
            return $("#ch-username-password-input").val();
        }

    function blockSubmission(){
        submissionAllowed = false;
        setButtonState();
    }

    function setButtonState(){
        document.getElementById("change-username-button").disabled = !submissionAllowed;
    }
})();