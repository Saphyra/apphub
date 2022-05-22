(function CreateEventController(){
    const REPETITION_TYPES = {
        ONE_TIME: ids.createEventOneTimeRepetitionTypeData,
        EVERY_X_DAYS: ids.createEventEveryXDaysRepetitionTypeData,
        DAYS_OF_WEEK: ids.createEventDaysOfWeekRepetitionTypeData
    }

    pageLoader.addLoader(addEventListeners, "Add eventListeners to CreateEvent page");

    let currentDate;

    window.createEventController = new function(){
        this.openCreateEventWindow = openCreateEventWindow;
        this.createEvent = createEvent;
    }

    function openCreateEventWindow(){
        currentDate = dailyTasksController.getCurrentDate();

        document.getElementById(ids.createEventTitleInput).value = "";
        document.getElementById(ids.createEventContentInput).value = "";
        document.getElementById(ids.createEventRepetitionTypeSelect).value = "ONE_TIME";
        document.getElementById(ids.createEventRepetitionTypeDaysInput).value = 1;
        $(".create-event-days-of-week-input").prop("checked", false);
        switchTab("repetition-type-data", REPETITION_TYPES.ONE_TIME);

        switchTab("main-page", ids.createEventPage);
    }

    function createEvent(){
        //TODO
    }

    function addEventListeners(){
        const repetitionTypeSelect = document.getElementById(ids.createEventRepetitionTypeSelect);
        repetitionTypeSelect.onchange = function(){
            switchTab("repetition-type-data", REPETITION_TYPES[repetitionTypeSelect.value]);
        }
    }
})();