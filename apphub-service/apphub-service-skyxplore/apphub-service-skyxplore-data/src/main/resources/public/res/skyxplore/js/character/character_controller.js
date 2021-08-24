(function CharacterController(){
    events.VALIDATION_ATTEMPT = "validation_attempt";

    let submissionAllowed = false;
    let validationTimeout = null;

    $(document).ready(init);

    window.characterController = new function(){
        this.save = save;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        validate,
        true
    ).setName("Auto-validator"));

    function init(){
        $("#" + ids.characterNameInput).on("keyup", function(){eventProcessor.processEvent(new Event(events.VALIDATION_ATTEMPT))});
        validate();
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.VALIDATION_ATTEMPT},
        function(){
            blockSubmission();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }
            validationTimeout = setTimeout(validate, getValidationTimeout());
        }
    ).setName("FormValidator"));

    function validate(){
        const name = document.getElementById(ids.characterNameInput).value;

        const allValid = new Stream([
                function(){return validateName(name)}
            ])
            .map(function(validator){return validator()})
            .peek(function(validationResult){validationResult.process()})
            .allMatch(function(validationResult){return validationResult.isValid});

        submissionAllowed = allValid;
        setSubmissionButtonState();

        function validateName(name){
            if(name.length < 3){
                return {
                    isValid: false,
                    process: createErrorProcess(ids.invalidName, "character-name-too-short")
                }
            }

            if(name.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(ids.invalidName, "character-name-too-long")
                }
            }

            return {
                isValid: true,
                process: createSuccessProcess(ids.invalidName)
            }
        }
    }

    function blockSubmission(){
        submissionAllowed = false;
        setSubmissionButtonState();
    }

    function setSubmissionButtonState(){
        document.getElementById(ids.submissionButton).disabled = !submissionAllowed;
    }

    function save(){
        if(!submissionAllowed){
            return;
        }

        const name = document.getElementById(ids.characterNameInput).value;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_CREATE_OR_UPDATE_CHARACTER"), {name: name});
            request.processValidResponse = function(){
                sessionStorage.successMessage = "character-saved";
                window.location.href = Mapping.SKYXPLORE_PAGE;
            }
        dao.sendRequestAsync(request);
    }
})();