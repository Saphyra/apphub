const ids = {
        memoryReports: "memory-reports",
        itemsToDisplay: "items-to-display",
    }

scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/admin-panel/js/memory_monitoring/memory_monitoring_controller.js");

(function PageController(){
    $(document).ready(function(){
        localization.loadLocalization("admin-panel", "memory_monitoring");
    });
})();