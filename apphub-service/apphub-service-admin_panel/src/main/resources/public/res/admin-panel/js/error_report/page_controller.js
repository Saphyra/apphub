(function PageController(){
    scriptLoader.loadScript("/res/admin-panel/js/error_report/report_controller.js");

    window.ids = {
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
        errorReportResponseStatus: "error-report-response-status",
        errorReportResponseBody: "error-report-response-body",
        errorReportException: "error-report-exception",
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "admin_panel", fileName: "error_report"}));
    });
})();