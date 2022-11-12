(function BulkOperationsController(){
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
        
        this.deleteChecked = deleteChecked;

        this.markAsRead = function(){
            markErrorReports(getCheckedIds(), "READ", reportController.search);
        }

        this.mark = function(){
            markErrorReports(getCheckedIds(), "MARKED", reportController.search);
        }

        this.markAsUnread = function(){
            markErrorReports(getCheckedIds(), "UNREAD", reportController.search);
        }

        this.deleteAll = deleteAll;
    }

    function deleteChecked(){
        const checkedIds =  getCheckedIds();

        reportController.deleteReports(checkedIds);
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
                const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_DELETE_READ_ERROR_REPORTS"));
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

    function deleteAll(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-all-error-reports-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("delete-all-error-reports-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("delete-all-error-reports-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-all-error-reports-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-all-error-reports-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_ERROR_REPORT_DELETE_ALL"));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("all-deleted"));
                        reportController.search();
                    };
                dao.sendRequestAsync(request);
            }
        )
    }
})();