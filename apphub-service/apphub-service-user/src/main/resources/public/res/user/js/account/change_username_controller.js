(function ChangeUsernameController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");
    
    const INVALID_USERNAME = "#ch-username-invalid-new-username";
    const INVALID_PASSWORD = "#ch-username-invalid-password";

    events.CHANGE_USERNAME_ATTEMPT = "change_username_attempt";
    events.CHANGE_USERNAME_VALIDATION_ATTEMPT = "change_username_validation_attempt";

    let submissionAllowed = false;
    let validationTimeout = null;

    pageLoader.addLoader(function(){
        $(".change-username-input").on("keyup", function(e){
            if(e.which == 13){
                eventProcessor.processEvent(new Event(events.CHANGE_USERNAME_ATTEMPT));
            }else{
                eventProcessor.processEvent(new Event(events.CHANGE_USERNAME_VALIDATION_ATTEMPT));
            }
        });
        $(".change-username-input").on("focusin", function(){
            eventProcessor.processEvent(new Event(events.CHANGE_USERNAME_VALIDATION_ATTEMPT));
        });
    }, "ChangeUsername add event listeners");

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CHANGE_USERNAME_VALIDATION_ATTEMPT},
        function(){
            blockSubmission();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }
            validationTimeout = setTimeout(validateInputs, getValidationTimeout());
        }
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CHANGE_USERNAME_ATTEMPT},
        function(){
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
                    notificationService.showSuccess(Localization.getAdditionalContent("username-changed"));
                }
            dao.sendRequestAsync(request);
            blockSubmission();
        }
    ));

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