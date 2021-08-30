(function ChangePasswordController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");

    const INVALID_NEW_PASSWORD = "#ch-password-invalid-new-password";
    const INVALID_CONFIRM_PASSWORD = "#ch-password-invalid-confirm-password";
    const INVALID_PASSWORD = "#ch-password-invalid-password";

    let submissionAllowed = false;
    let validationTimeout = null;

    pageLoader.addLoader(function(){
        $(".change-password-input").on("keyup", function(e){
            if(e.which == 13){
                changePasswordAttempt();
            }else{
                changePasswordValidationAttempt();
            }
        });
        $(".change-password-input").on("focusin", function(){
            changePasswordValidationAttempt();
        });
    }, "ChangePassword add event listeners");

    window.changePasswordController = new function(){
        this.changePasswordAttempt = changePasswordAttempt;
    }

    function changePasswordValidationAttempt(){
        blockSubmission();

        if(validationTimeout){
            clearTimeout(validationTimeout);
        }
        validationTimeout = setTimeout(validateInputs, getValidationTimeout());
    }

    function changePasswordAttempt(){
        if(!submissionAllowed){
            return;
        }

        const payload = {
            newPassword: getNewPassword(),
            password: getPassword()
        }

        $(".change-password-input").val("");

        const request = new Request(Mapping.getEndpoint("ACCOUNT_CHANGE_PASSWORD"), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("password-changed"));
            }
        dao.sendRequestAsync(request);
        blockSubmission();
    }

    function validateInputs(){
        const newPassword = getNewPassword();
        const confirmPassword = getConfirmPassword();
        const password = getPassword();

        const validationResults = validationService.bulkValidation([
            function(){return validationService.validatePassword(INVALID_NEW_PASSWORD, newPassword)},
            function(){return validationService.validateConfirmPassword(INVALID_CONFIRM_PASSWORD, newPassword, confirmPassword)},
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

    function getNewPassword(){
        return $("#ch-password-new-password-input").val();
    }

    function getConfirmPassword(){
        return $("#ch-password-confirm-password-input").val();
    }

    function getPassword(){
            return $("#ch-password-password-input").val();
        }

    function blockSubmission(){
        submissionAllowed = false;
        setButtonState();
    }

    function setButtonState(){
        document.getElementById("change-password-button").disabled = !submissionAllowed;
    }
})();