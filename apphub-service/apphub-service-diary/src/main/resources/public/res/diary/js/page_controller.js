window.ids = {
    calendar: "calendar",
    calendarCurrentMonth: "calendar-current-month",
    dailyTasks: "daily-tasks",
    dailyTasksCurrentDay: "daily-tasks-current-day",
    createEventPage: "create-event-page",
    mainPage: "main-page",
    createEventTitleInput: "create-event-title-input",
    createEventContentInput: "create-event-content-input",
    createEventRepetitionTypeSelect: "create-event-repetition-type-select",
    createEventRepetitionTypeDaysInput: "create-event-repetition-type-days-input",
    createEventOneTimeRepetitionTypeData: "create-event-one-time-repetition-type-data",
    createEventEveryXDaysRepetitionTypeData: "create-event-every-x-days-repetition-type-data",
    createEventDaysOfWeekRepetitionTypeData: "create-event-days-of-week-repetition-type-data",
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
        this.displayMainPage = function(){
            switchTab("main-page", ids.mainPage);
        }
    }
})();