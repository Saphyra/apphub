(function ChangeEmailController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");

    const INVALID_EMAIL = "ch-email-invalid-new-email";
    const INVALID_PASSWORD = "ch-email-invalid-password";

    events.CHANGE_EMAIL_ATTEMPT = "change_email_attempt";
    events.CHANGE_EMAIL_VALIDATION_ATTEMPT = "change_email_validation_attempt";

    let submissionAllowed = false;
    let validationTimeout = null;

    $(document).ready(function(){
        $(".change-email-input").on("keyup", function(e){
            if(e.which == 13){
                eventProcessor.processEvent(new Event(events.CHANGE_EMAIL_ATTEMPT));
            }else{
                eventProcessor.processEvent(new Event(events.CHANGE_EMAIL_VALIDATION_ATTEMPT));
            }
        });
        $(".change-email-input").on("focusin", function(){
            eventProcessor.processEvent(new Event(events.CHANGE_EMAIL_VALIDATION_ATTEMPT));
        });
    });

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CHANGE_EMAIL_VALIDATION_ATTEMPT},
        function(){
            blockSubmission();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }
            validationTimeout = setTimeout(validateInputs, getValidationTimeout());
        }
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CHANGE_EMAIL_ATTEMPT},
        function(){
            if(!submissionAllowed){
                return;
            }

            const payload = {
                email: getEmail(),
                password: getPassword()
            }

            $("#ch-email-password-input").val("");

            const request = new Request(Mapping.getEndpoint("CHANGE_EMAIL"), payload);
                request.processValidResponse = function(){
                    notificationService.showSuccess(Localization.getAdditionalContent("email-changed"))
                }
            dao.sendRequestAsync(request);
            blockSubmission();
        }
    ));

    function validateInputs(){
        const email = getEmail();
        const password = getPassword();

        const validationResults = validationService.bulkValidation([
            function(){return validationService.validateEmail(INVALID_EMAIL, email)},
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

    function getEmail(){
        return $("#ch-email-new-email-input").val();
    }

    function getPassword(){
            return $("#ch-email-password-input").val();
        }

    function blockSubmission(){
        submissionAllowed = false;
        setButtonState();
    }

    function setButtonState(){
        document.getElementById("change-email-button").disabled = !submissionAllowed;
    }
})();