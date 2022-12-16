(function PageController(){
    scriptLoader.loadScript("/res/common/js/logout_service.js");

    $(document).ready(function(){
        localization.loadLocalization("common", "error");
    });
})();