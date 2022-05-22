window.ids = {
    calendar: "calendar",
    calendarCurrentMonth: "calendar-current-month",
    dailyTasks: "daily-tasks",
    dailyTasksCurrentDay: "daily-tasks-current-day",
}

scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/diary/js/local_date.js");

window.monthLocalization = new CustomLocalization("diary", "months");
const CURRENT_DATE = new LocalDate(new Date());

scriptLoader.loadScript("/res/diary/js/calendar_controller.js");
scriptLoader.loadScript("/res/diary/js/daily_tasks_controller.js");
scriptLoader.loadScript("/res/diary/js/create_event_controller.js");

(function PageController(){
    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "diary", fileName: "diary"}));
    });

    window.pageController = new function(){
    }
})();