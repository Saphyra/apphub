window.ids = {
    calendar: "calendar",
    calendarCurrentMonth: "calendar-current-month",
}

scriptLoader.loadScript("/res/diary/js/calendar_controller.js");

window.monthLocalization = new CustomLocalization("diary", "months");

(function PageController(){
    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "diary", fileName: "diary"}));
    });

    window.pageController = new function(){
    }
})();