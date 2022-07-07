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
        const title = document.getElementById(ids.createEventTitleInput).value;
        const content = document.getElementById(ids.createEventContentInput).value;
        const repetitionType = document.getElementById(ids.createEventRepetitionTypeSelect).value;
        const repetitionDays = document.getElementById(ids.createEventRepetitionTypeDaysInput).value;
        const repetitionDaysOfWeek = getCheckedDays();

        if(isBlank(title)){
            notificationService.showError(Localization.getAdditionalContent("empty-title"));
            return;
        }

        switch(repetitionType){
            case "EVERY_X_DAYS":
                if(repetitionDays < 1){
                    notificationService.showError(Localization.getAdditionalContent("repetition-type-days-too-low"));
                    return;
                }
                break;
            case "DAYS_OF_WEEK":
                if(repetitionDaysOfWeek.length < 1){
                    notificationService.showError(Localization.getAdditionalContent("no-day-selected"));
                    return;
                }
                break;
            case "ONE_TIME":
                break;
            default:
                throwException("IllegalArgument", "Unhandled repetitionType: " + repetitionType);
        }

        const payload = {
            referenceDate: calendarController.getCurrentDate().toString(),
            date: currentDate.toString(),
            title: title,
            content: content,
            repetitionType: repetitionType,
            repetitionDays: repetitionDays,
            repetitionDaysOfWeek: repetitionDaysOfWeek
        }

        const request = new Request(Mapping.getEndpoint("DIARY_CREATE_EVENT"), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(days){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                notificationService.showSuccess(Localization.getAdditionalContent("event-created"));
                pageController.displayMainPage();
            }
        dao.sendRequestAsync(request);

        function getCheckedDays(){
            const result = [];

            $("#create-event-days-of-week-repetition-type-data .create-event-days-of-week-input:checked")
                .each(function(){result.push($(this).val())});

            return result;
        }
    }

    function addEventListeners(){
        const repetitionTypeSelect = document.getElementById(ids.createEventRepetitionTypeSelect);
        repetitionTypeSelect.onchange = function(){
            switchTab("repetition-type-data", REPETITION_TYPES[repetitionTypeSelect.value]);
        }
    }
})();