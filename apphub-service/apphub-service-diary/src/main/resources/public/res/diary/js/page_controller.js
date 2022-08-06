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
scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/common/js/date.js");

window.monthLocalization = new CustomLocalization("diary", "months");
const CURRENT_DATE = LocalDate.create(new Date());

scriptLoader.loadScript("/res/diary/js/calendar_controller.js");
scriptLoader.loadScript("/res/diary/js/daily_tasks_controller.js");
scriptLoader.loadScript("/res/diary/js/create_event_controller.js");
scriptLoader.loadScript("/res/diary/js/view_event_controller.js");

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