(function DeleteAccountController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");

    const INVALID_PASSWORD = "#delete-account-invalid-password";

    events.DELETE_ACCOUNT_ATTEMPT = "delete_account_attempt";
    events.DELETE_ACCOUNT_VALIDATION_ATTEMPT = "delete_account_validation_attempt";

    let submissionAllowed = false;
    let validationTimeout = null;

    $(document).ready(function(){
        $(".delete-account-input").on("keyup", function(e){
            if(e.which == 13){
                eventProcessor.processEvent(new Event(events.DELETE_ACCOUNT_ATTEMPT));
            }else{
                eventProcessor.processEvent(new Event(events.DELETE_ACCOUNT_VALIDATION_ATTEMPT));
            }
        });
        $(".delete-account-input").on("focusin", function(){
            eventProcessor.processEvent(new Event(events.DELETE_ACCOUNT_VALIDATION_ATTEMPT));
        });
    });

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.DELETE_ACCOUNT_VALIDATION_ATTEMPT},
        function(){
            blockSubmission();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }
            validationTimeout = setTimeout(validateInputs, getValidationTimeout());
        }
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.DELETE_ACCOUNT_ATTEMPT},
        function(){
            if(!submissionAllowed){
                return;
            }

            const payload = {
                value: getPassword()
            }

            $("#delete-account-password-input").val("");

            const request = new Request(Mapping.getEndpoint("DELETE_ACCOUNT"), payload);
                request.processValidResponse = function(){
                    sessionStorage.successMessage = "account-deleted";
                    window.location.href = Mapping.INDEX_PAGE;
                }
            dao.sendRequestAsync(request);
            blockSubmission();
        }
    ));

    function validateInputs(){
        const password = getPassword();

        const validationResults = validationService.bulkValidation([
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

    function getPassword(){
        return $("#delete-account-password-input").val();
    }

    function blockSubmission(){
        submissionAllowed = false;
        setButtonState();
    }

    function setButtonState(){
        document.getElementById("delete-account-button").disabled = !submissionAllowed;
    }
})();