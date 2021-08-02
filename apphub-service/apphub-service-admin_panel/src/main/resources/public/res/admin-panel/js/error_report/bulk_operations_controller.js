(function BulkOperationsController(){
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");

    window.bulkOperationsController = new function(){
        this.selectAll = function(e){
            e.target.checked = true;
            select(true);
        }
        this.deselectAll = function(e){
            e.target.checked = false;
            e.preventDefault();
            select(false);
        }

        this.deleteAllRead = deleteAllRead;
        this.markAsRead = function(){
            markErrorReports(getCheckedIds(), "READ", reportController.search);
        }

        this.mark = function(){
            markErrorReports(getCheckedIds(), "MARKED", reportController.search);
        }

        this.markAsUnread = function(){
            markErrorReports(getCheckedIds(), "UNREAD", reportController.search);
        }
    }

    function deleteAllRead(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-all-read-error-reports-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("delete-all-read-error-reports-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("delete-all-read-error-reports-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-all-read-error-reports-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-all-read-error-reports-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("ERROR_REPORT_DELETE_ALL_READ"));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("all-read-deleted"));
                        reportController.search();
                    };
                dao.sendRequestAsync(request);
            }
        )
    }

    function select(value){
        $(".error-report-selection").prop("checked", value);
    }

    function getCheckedIds(){
        const result = [];

        $(".error-report-selection:checked").each(function(){result.push($(this).val())});

        return result;
    }
})();