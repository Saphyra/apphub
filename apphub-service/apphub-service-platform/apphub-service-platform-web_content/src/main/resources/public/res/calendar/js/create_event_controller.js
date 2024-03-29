(function CreateEventController(){
    const REPETITION_TYPES = {
        ONE_TIME: ids.createEventOneTimeRepetitionTypeData,
        EVERY_X_DAYS: ids.createEventEveryXDaysRepetitionTypeData,
        DAYS_OF_WEEK: ids.createEventDaysOfWeekRepetitionTypeData,
        DAYS_OF_MONTH: ids.createEventDaysOfMonthRepetitionTypeData
    }

    const daysOfMonthSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.selectedDaysOfMonth)
        .withGetKeyMethod((day) => {return day})
        .withCreateNodeMethod(createDayOfMonthNode)
        .withSortMethod((a, b) => {return a.localeCompare(b)})
        .withIdPrefix("selected-day-of-month")
        .build();

    pageLoader.addLoader(addEventListeners, "Add eventListeners to CreateEvent page");

    let currentDate;

    window.createEventController = new function(){
        this.openCreateEventWindow = openCreateEventWindow;
        this.createEvent = createEvent;
        this.addDayOfMonth = addDayOfMonth;
    }

    function openCreateEventWindow(){
        currentDate = dailyTasksController.getCurrentDate();

        document.getElementById(ids.createEventTitleInput).value = "";
        document.getElementById(ids.createEventContentInput).value = "";
        document.getElementById(ids.createEventRepetitionTypeSelect).value = "ONE_TIME";
        document.getElementById(ids.createEventRepetitionTypeDaysInput).value = 1;
        document.getElementById(ids.createEventTimeHours).value = "";
        document.getElementById(ids.createEventTimeMinutes).value = "";
        document.getElementById(ids.repeatForDays).value = 1;
        $(".create-event-days-of-week-input").prop("checked", false);
        switchTab("repetition-type-data", REPETITION_TYPES.ONE_TIME);
        daysOfMonthSyncEngine.clear();

        switchTab("main-page", ids.createEventPage);
    }

    function createEvent(){
        const title = document.getElementById(ids.createEventTitleInput).value;
        const content = document.getElementById(ids.createEventContentInput).value;
        const repetitionType = document.getElementById(ids.createEventRepetitionTypeSelect).value;
        const repetitionDays = document.getElementById(ids.createEventRepetitionTypeDaysInput).value;
        const repetitionDaysOfWeek = getCheckedDays();
        const repetitionDaysOfMonth = daysOfMonthSyncEngine.keys();
        const timeHours = replaceWithNullIfEmpty(document.getElementById(ids.createEventTimeHours).value);
        const timeMinutes = replaceWithNullIfEmpty(document.getElementById(ids.createEventTimeMinutes).value);
        const repeatForDays = document.getElementById(ids.repeatForDays).value;

        if(isBlank(title)){
            notificationService.showError(localization.getAdditionalContent("empty-title"));
            return;
        }

        switch(repetitionType){
            case "EVERY_X_DAYS":
                if(repetitionDays < 1){
                    notificationService.showError(localization.getAdditionalContent("repetition-type-days-too-low"));
                    return;
                }
                break;
            case "DAYS_OF_WEEK":
                if(repetitionDaysOfWeek.length < 1){
                    notificationService.showError(localization.getAdditionalContent("no-day-selected"));
                    return;
                }
                break;
            case "DAYS_OF_MONTH":
                if(repetitionDaysOfMonth.length < 1){
                    notificationService.showError(localization.getAdditionalContent("no-day-selected"));
                    return;
                }
            break;
            case "ONE_TIME":
                break;
            default:
                throwException("IllegalArgument", "Unhandled repetitionType: " + repetitionType);
        }

        if((timeHours == null && timeMinutes != null) || (timeHours != null && timeMinutes == null)){
            notificationService.showError(localization.getAdditionalContent("empty-time"));
            return;
        }

        const payload = {
            referenceDate: {
                month: calendarController.getCurrentDate().toString(),
                day: currentDate.toString()
            },
            date: currentDate.toString(),
            title: title,
            content: content,
            repetitionType: repetitionType,
            repetitionDays: repetitionDays,
            repetitionDaysOfWeek: repetitionDaysOfWeek,
            repetitionDaysOfMonth: repetitionDaysOfMonth,
            hours: timeHours,
            minutes: timeMinutes,
            repeat: repeatForDays
        }

        const request = new Request(Mapping.getEndpoint("CALENDAR_CREATE_EVENT"), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(days){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                notificationService.showSuccess(localization.getAdditionalContent("event-created"));
                pageController.displayMainPage();
            }
        dao.sendRequestAsync(request);

        function getCheckedDays(){
            const result = [];

            $("#create-event-days-of-week-repetition-type-data .create-event-days-of-week-input:checked")
                .each(function(){result.push($(this).val())});

            return result;
        }

        function replaceWithNullIfEmpty(value){
            return value === "" ? null : value;
        }
    }

    function addDayOfMonth(){
        const value = document.getElementById(ids.createEventRepetitionTypeDaysOfMonthInput).value;

        if(value < 1 || value > 31){
            return;
        }

        daysOfMonthSyncEngine.add(value);
    }

    function createDayOfMonthNode(day){
        const node = document.createElement("DIV");
            node.classList.add("selected-day-of-month");
            node.classList.add("button");

            node.innerText = day;

            node.onclick = function(){
                daysOfMonthSyncEngine.remove(day);
            }
        return node;
    }

    function addEventListeners(){
        const repetitionTypeSelect = document.getElementById(ids.createEventRepetitionTypeSelect);
        repetitionTypeSelect.onchange = function(){
            switchTab("repetition-type-data", REPETITION_TYPES[repetitionTypeSelect.value]);
        }
    }
})();