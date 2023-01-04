(function DeleteAccountController(){
    scriptLoader.loadScript("/res/user/js/common/validation_service.js");
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");

    const INVALID_PASSWORD = "#delete-account-invalid-password";
    const ACCOUNT_DELETION_CONFIRMATION_DIALOG_ID = "account-deletion-confirmation-dialog";

    let submissionAllowed = false;
    let validationTimeout = null;

    pageLoader.addLoader(function(){
        $(".delete-account-input").on("keyup", function(e){
            if(e.which == 13){
                deleteAccountAttempt();
            }else{
                deleteAccountValidationAttempt();
            }
        });
        $(".delete-account-input").on("focusin", function(){
            deleteAccountValidationAttempt();
        });
    }, "DeleteAccount add validation event listeners");

    window.deleteAccountController = new function(){
        this.deleteAccountAttempt = deleteAccountAttempt;
    }

    function deleteAccountValidationAttempt(){
        blockSubmission();

        if(validationTimeout){
            clearTimeout(validationTimeout);
        }
        validationTimeout = setTimeout(validateInputs, getValidationTimeout());
    }

    function deleteAccountAttempt(){
        if(!submissionAllowed){
            return;
        }

        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("account-deletion-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("account-deletion-confirmation-dialog-detail"))
            .withConfirmButton(localization.getAdditionalContent("account-deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("account-deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            ACCOUNT_DELETION_CONFIRMATION_DIALOG_ID,
            confirmationDialogLocalization,
            function(){
                const payload = {
                    value: getPassword()
                }

                $("#delete-account-password-input").val("");

                const request = new Request(Mapping.getEndpoint("ACCOUNT_DELETE_ACCOUNT"), payload);
                    request.getErrorHandler()
                        .addErrorHandler(new ErrorHandler(
                            (request, response) => {
                                console.log("Response body: ", response.body);
                                return response.body.indexOf("ACCOUNT_LOCKED") > -1
                            },
                            (request, response) => {
                                const errorResponse = JSON.parse(response.body);
                                notificationService.storeErrorText(errorResponse.localizedMessage);
                                eventProcessor.processEvent(new Event(events.LOGOUT));
                            }
                        ));
                    request.processValidResponse = function(){
                        sessionStorage.successMessage = "account-deleted";
                        window.location.href = Mapping.INDEX_PAGE;
                    }
                dao.sendRequestAsync(request);
                blockSubmission();
            },
            function(){
                $("#delete-account-password-input").val("");
                blockSubmission();
            }
        );
    }

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