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
    createEventRepetitionTypeDaysOfMonthInput: "create-event-repetition-type-days-of-month-input",
    createEventOneTimeRepetitionTypeData: "create-event-one-time-repetition-type-data",
    createEventEveryXDaysRepetitionTypeData: "create-event-every-x-days-repetition-type-data",
    createEventDaysOfWeekRepetitionTypeData: "create-event-days-of-week-repetition-type-data",
    createEventDaysOfMonthRepetitionTypeData: "create-event-days-of-month-repetition-type-data",
    viewEventPage: "view-event-page",
    viewEventTitle: "view-event-title",
    viewEventContent: "view-event-content",
    viewEventNote: "view-event-note",
    viewEventEditButton: "view-event-edit-button",
    viewEventDeleteButton: "view-event-delete-button",
    viewEventDoneButton: "view-event-done-button",
    viewEventNotDoneButton: "view-event-not-done-button",
    viewEventSnoozeButton: "view-event-snooze-button",
    viewEventUnsnoozeButton: "view-event-unsnooze-button",
    viewEventSaveButton: "view-event-save-button",
    viewEventDiscardButton: "view-event-discard-button",
    selectedDaysOfMonth: "selected-days-of-month",
    createEventTimeHours: "create-event-time-hours",
    createEventTimeMinutes: "create-event-time-minutes",
    searchResultPage: "search-result-page",
    searchResult: "search-result",
    searchInput: "search-input",
    searchResultPageSearchInput: "search-result-page-search-input",
    repeatForDays: "repeat-for-days",
}

window.occurrenceOrder = new function(){
    const orders = {
        EXPIRED: 1,
        VIRTUAL: 2,
        PENDING: 2,
        DONE: 3,
        SNOOZED: 4
    }

    this.getOrder = function(status){
        return orders[status] || throwException("IllegalArgument", status + " has no order defined.");
    }
}

events.EVENT_CHANGED = "event-changed";

scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/common/js/date.js");

window.monthLocalization = localization.loadCustomLocalization("diary", "months");
const CURRENT_DATE = LocalDate.now();

scriptLoader.loadScript("/res/diary/js/calendar_controller.js");
scriptLoader.loadScript("/res/diary/js/daily_tasks_controller.js");
scriptLoader.loadScript("/res/diary/js/create_event_controller.js");
scriptLoader.loadScript("/res/diary/js/view_event_controller.js");
scriptLoader.loadScript("/res/diary/js/search_controller.js");

(function PageController(){
    $(document).ready(function(){
        localization.loadLocalization("diary", "diary");

        window.addEventListener("focus", refreshPageIfNeeded);
    });

    window.pageController = new function(){
        this.displayMainPage = function(){
            switchTab("main-page", ids.mainPage);
        }
    }

    function refreshPageIfNeeded(){
        console.log("Checking if page needs to be refreshed...");

        const currentDate = LocalDate.now();

        if(!currentDate.equals(CURRENT_DATE)){
            window.location.reload();
        }
    }
})();

function parseTime(time){
    if(time == null){
        return "";
    }

    const splitted = time.split(":");

    return " " + splitted[0] + ":" + splitted[1];
}

function occurrenceComparator(a, b){
    let result = occurrenceOrder.getOrder(a.status) - occurrenceOrder.getOrder(b.status);

    if(result == 0){
        if(a.time == b.time){
            return 0;
        }

        if(a.time == null){
            return 1;
        }

        return a.time.localeCompare(b.time);
    }

    return result;
}