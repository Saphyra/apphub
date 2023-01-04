(function PageController(){
    scriptLoader.loadScript("/res/utils/js/log_formatter/format_controller.js");
    scriptLoader.loadScript("/res/common/js/animation/move_controller.js");

    window.ids = {
        logInput: "input",
        resultContainer: "result",
        filterParameterSelect: "filter-parameter-select",
        filterSearchOperation: "filter-search-operation",
        filterInput: "filter-input",
        addFilterRuleButton: "add-filter-rule-button",
        filterRuleContainer: "filter-rule-container",
        visibilityContainer: "display-field-buttons-container",
        orderButtons: "order-buttons",
        orderType: "order-type",
    }

    window.pageController = new function(){

    }

    $(document).ready(function(){
        localization.loadLocalization("utils", "log_formatter");

        addRightClickMove("result-container", "result");
        document.getElementById("result-container")
            .addEventListener('contextmenu', event => event.preventDefault());
    });
})();