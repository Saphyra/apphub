(function ReportController(){
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");

    let pageNumber = 1;
    let openedErrorReportId = null;

    window.reportController = new function(){
        this.search = loadCurrentPage;
        this.nextPage = nextPage;
        this.previousPage = previousPage;
        this.closeDetailsPage = function(){
            loadCurrentPage();
            switchTab("main-page", ids.errorReportOverview);
        }
        this.deleteOpenedErrorReport = function(){
            deleteReports([openedErrorReportId]);
        }
        this.markOpenedErrorReport = function(status){
            markErrorReports([openedErrorReportId], status);
        }
    }

    $(document).ready(init);

    function nextPage(){
        pageNumber++;
        document.getElementById(ids.previousButton).disabled = false;
        displayPageNumber();
        loadCurrentPage();
    }

    function previousPage(){
        pageNumber--;
        if(pageNumber < 2){
            document.getElementById(ids.previousButton).disabled = true;
            pageNumber = 1;
        }
        displayPageNumber();
        loadCurrentPage();
    }

    function loadCurrentPage(){
        const payload = {
            message: nullIfEmpty(document.getElementById(ids.searchByMessage).value),
            statusCode: nullIfEmpty(document.getElementById(ids.searchByStatusCode).value),
            startTime: nullIfEmpty(document.getElementById(ids.searchByStartTime).value),
            endTime: nullIfEmpty(document.getElementById(ids.searchByEndTime).value),
            status: nullIfEmpty(document.querySelector('input[name="status-query"]:checked').value),
            pageSize: document.getElementById(ids.objectsPerPage).value,
            page: pageNumber
        }

        const request = new Request(Mapping.getEndpoint("ERROR_REPORT_GET_ERRORS"), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = displayErrors;
        dao.sendRequestAsync(request);
    }

    function displayErrors(errors){
        const container = document.getElementById(ids.errorReports);
            container.innerHTML = "";

            new Stream(errors)
                .map(createRow)
                .forEach(function(row){container.appendChild(row)});

        function createRow(errorReport){
            const row = document.createElement("TR");
                row.classList.add(errorReport.status.toLowerCase());

                const checkboxCell = document.createElement("TD");
                    const checkbox = document.createElement("INPUT");
                        checkbox.type = "checkbox";
                        checkbox.value = errorReport.id;
                        checkbox.classList.add("error-report-selection");
                checkboxCell.appendChild(checkbox);
            row.appendChild(checkboxCell);

                const createdAtCell = document.createElement("TD");
                    createdAtCell.classList.add("nowrap");
                    createdAtCell.innerText = errorReport.createdAt;
            row.appendChild(createdAtCell);

                const responseStatusCell = document.createElement("TD");
                    responseStatusCell.innerText = errorReport.responseStatus;
            row.appendChild(responseStatusCell);

                const messageCell = document.createElement("TD");
                    messageCell.classList.add("left");
                    messageCell.innerText = errorReport.message;
            row.appendChild(messageCell);

                const operationsCell = document.createElement("TD");
            row.appendChild(operationsCell);

            row.onclick = function(){
                viewErrorReport(errorReport.id);
            }
            return row;
        }
    }

    function viewErrorReport(id){
        const request = new Request(Mapping.getEndpoint("ERROR_REPORT_GET_ERROR", {id: id}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = displayErrorReport;
        dao.sendRequestAsync(request);

        function displayErrorReport(errorReport){
            openedErrorReportId = errorReport.id;

            document.getElementById(ids.errorReportId).innerText = errorReport.id;
            document.getElementById(ids.errorReportCreatedAt).innerText = errorReport.createdAt;
            document.getElementById(ids.errorReportMessage).innerText = errorReport.message;
            document.getElementById(ids.errorReportResponseStatus).innerText = errorReport.responseStatus;
            document.getElementById(ids.errorReportResponseBody).innerText = errorReport.responseBody;

            parseException(errorReport.exception);

            switchTab("main-page", ids.errorReportDetails);

            function parseException(exception){
                const container = document.getElementById(ids.errorReportException);
                    container.innerHTML = "";

                if(exception){
                    const exceptionWrapper = parseStackTrace(exception);
                    container.appendChild(exceptionWrapper);
                }

                function parseStackTrace(exception, prefix){
                    const wrapper = document.createElement("DIV");
                        wrapper.classList.add("exception-wrapper");

                        const titleElement = document.createElement("DIV");
                            titleElement.classList.add("exception-title");
                            titleElement.innerText = (prefix ? prefix + " " : "") + exception.type + ": " + exception.message;
                    wrapper.appendChild(titleElement);

                        const stackTraceWrapper = document.createElement("DIV");
                            stackTraceWrapper.classList.add("stack-trace-wrapper")

                            new Stream(exception.stackTrace)
                                .map(createStackTraceElement)
                                .forEach(function(element){stackTraceWrapper.appendChild(element)});
                    wrapper.appendChild(stackTraceWrapper);

                        if(exception.cause){
                            const causeWrapper = document.createElement("DIV");
                                causeWrapper.classList.add("cause-wrapper");

                                const causeNode = parseStackTrace(exception.cause, "Caused by: ");
                            causeWrapper.appendChild(causeNode);
                            wrapper.appendChild(causeNode);
                        }

                    return wrapper;

                    function createStackTraceElement(stackTraceElement){
                        const node = document.createElement("DIV");
                            node.classList.add("stack-trace-item");

                            node.innerText = "at " + stackTraceElement.className + "." + stackTraceElement.methodName + "(" + stackTraceElement.fileName + ":" + stackTraceElement.lineNumber + ")";
                        return node;
                    }
                }
            }
        }
    }

    function deleteReports(ids){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-error-reports-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("delete-error-reports-confirmation-dialog-detail", {ids: ids}))
            .withConfirmButton(Localization.getAdditionalContent("delete-error-reports-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-error-reports-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-error-reports-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("ERROR_REPORT_DELETE_ERRORS"), ids);
                    request.processValidResponse = reportController.closeDetailsPage;
                dao.sendRequestAsync(request);
            }
        )
    }

    function markErrorReports(ids, status){
        const request = new Request(Mapping.getEndpoint("ERROR_REPORT_MARK_ERRORS", {status: status}), ids);
            request.processValidResponse = function(){
                Localization.getAdditionalContent("error-reports-marked");
            }
        dao.sendRequestAsync(request);
    }

    function displayPageNumber(){
        document.getElementById(ids.pageNumber).innerText = pageNumber;
    }

    function nullIfEmpty(input){
        return input && input.length > 0 ? input : null;
    }

    function init(){
        loadCurrentPage();
        displayPageNumber();
    }
})();