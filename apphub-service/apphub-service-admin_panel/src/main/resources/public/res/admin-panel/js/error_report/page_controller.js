scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/admin-panel/js/error_report/report_controller.js");
scriptLoader.loadScript("/res/admin-panel/js/error_report/bulk_operations_controller.js");

(function PageController(){
    window.ids = {
        searchByService: "search-by-service",
        searchByMessage: "search-by-message",
        searchByStatusCode: "search-by-status-code",
        searchByStartTime: "search-by-start-time",
        searchByEndTime: "search-by-end-time",
        pageNumber: "page-number",
        objectsPerPage: "objects-per-page",
        errorReports: "error-reports",
        previousButton: "previous-button",
        errorReportDetails: "error-report-details",
        errorReportOverview: "error-report-overview",
        errorReportId: "error-report-id",
        errorReportCreatedAt: "error-report-created-at",
        errorReportMessage: "error-report-message",
        errorReportService: "error-report-service",
        errorReportResponseStatus: "error-report-response-status",
        errorReportResponseBody: "error-report-response-body",
        errorReportException: "error-report-exception",
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "error_report"}));
    });
})();

function markErrorReports(ids, status, callback){
    if(ids.length == 0){
        return;
    }

    const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_MARK_ERROR_REPORTS", {status: status}), ids);
        request.processValidResponse = function(){
            Localization.getAdditionalContent("error-reports-marked");

            if(callback){
                callback();
            }
        }
    dao.sendRequestAsync(request);
}