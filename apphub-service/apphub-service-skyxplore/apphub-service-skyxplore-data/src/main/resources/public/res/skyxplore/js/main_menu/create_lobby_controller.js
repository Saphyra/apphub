(function CreateLobbyController(){
    $(document).ready(init);

    let submissionAllowed = false;
    let validationTimeout = null;

    window.createLobbyController = new function(){
        this.newGame = function(){
            switchTab("main-page", "lobby-creation");

            document.getElementById(ids.lobbyNameInput).value = "";
            document.getElementById(ids.createLobbyButton).enabled = false;
        }

        this.createLobby = function(){
            if(!submissionAllowed){
                return;
            }

            const payload = {
                value: document.getElementById(ids.lobbyNameInput).value
            }

            const request = new Request(Mapping.getEndpoint("SKYXPLORE_CREATE_LOBBY"), payload);
                request.processValidResponse = function(){
                    window.location.href='/web/skyxplore/lobby'
                }
            dao.sendRequestAsync(request);
        }
    }

    function validationAttempt(){
        blockSubmission();

        if(validationTimeout){
            clearTimeout(validationTimeout);
        }
        validationTimeout = setTimeout(validate, getValidationTimeout());
    }

    function blockSubmission(){
        submissionAllowed = false;
        setSubmissionButtonState();
    }

    function setSubmissionButtonState(){
        document.getElementById(ids.createLobbyButton).disabled = !submissionAllowed;
    }

    function validate(){
        const name = document.getElementById(ids.lobbyNameInput).value;

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
                    process: createErrorProcess(ids.invalidLobbyName, "game-name-too-short")
                }
            }

            if(name.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(ids.invalidLobbyName, "game-name-too-long")
                }
            }

            return {
                isValid: true,
                process: createSuccessProcess(ids.invalidLobbyName)
            }
        }
    }

    function init(){
        $("#" + ids.lobbyNameInput).on("keyup", function(){validationAttempt()});
    }
})();