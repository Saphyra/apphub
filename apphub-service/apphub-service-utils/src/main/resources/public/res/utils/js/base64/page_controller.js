(function PageController(){
    scriptLoader.loadScript("/res/utils/js/base64/encode_controller.js");

    window.ids = {
        input: "input",
        result: "result",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        localization.loadLocalization("utils", "base4");
    });
})();