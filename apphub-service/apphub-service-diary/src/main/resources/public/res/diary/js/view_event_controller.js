(function ViewEventController(){
    let currentEvent = null;

    window.viewEventController = new function(){
        this.viewEvent = viewEvent;
        this.enableEditing = enableEditing;
        this.saveModifications = saveModifications;
        this.discardModifications = discardModifications;
        this.markAsDone = markAsDone;
        this.markAsDefault = markAsDefault;
        this.markAsSnoozed = markAsSnoozed;
        this.deleteEvent = deleteEvent;
    }

    function viewEvent(event){
        currentEvent = event;
        const title = document.getElementById(ids.viewEventTitle);
            title.innerText = event.title;
            title.contentEditable = false;
        const contentNode = document.getElementById(ids.viewEventContent);
            contentNode.value = event.content;
            contentNode.disabled = true;
        const noteNode = document.getElementById(ids.viewEventNote)
            noteNode.value = event.note;
            noteNode.disabled = true;

        $("#view-event-page footer button").hide();

        $("#" + ids.viewEventEditButton).show();
        $("#" + ids.viewEventDeleteButton).show();

        switch(event.status){
            case "VIRTUAL":
            case "PENDING":
            case "EXPIRED":
                $("#" + ids.viewEventDoneButton).show();
                $("#" + ids.viewEventSnoozeButton).show();
                break;
            case "DONE":
                $("#" + ids.viewEventNotDoneButton).show();
                break;
            case "SNOOZED":
                $("#" + ids.viewEventDoneButton).show();
                $("#" + ids.viewEventUnsnoozeButton).show();
                break;
            default:
                throwException("IllegalArgument", "Unhandled status " + event.status);
        }

        switchTab("main-page", ids.viewEventPage);
    }

    function enableEditing(){
        $("#" + ids.viewEventEditButton).hide();
        $("#" + ids.viewEventSaveButton).show();
        $("#" + ids.viewEventDiscardButton).show();

        document.getElementById(ids.viewEventTitle).contentEditable = true;
        document.getElementById(ids.viewEventContent).disabled = false;
        document.getElementById(ids.viewEventNote).disabled = false;
    }

    function saveModifications(){
        const title = document.getElementById(ids.viewEventTitle).innerText;
        const content = document.getElementById(ids.viewEventContent).value;
        const note = document.getElementById(ids.viewEventNote).value;

        if(isBlank(title)){
            notificationService.showError(Localization.getAdditionalContent("empty-title"));
            return;
        }

        const payload = {
            referenceDate: {
                month: calendarController.getCurrentDate().toString(),
                day: dailyTasksController.getCurrentDate().toString()
            },
            title: title,
            content: content,
            note: note
        }

        const request = new Request(Mapping.getEndpoint("DIARY_OCCURRENCE_EDIT", {occurrenceId: currentEvent.occurrenceId}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(days){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                currentEvent.title = title;
                currentEvent.content = content;
                currentEvent.note = note;
                viewEvent(currentEvent);
            }
        dao.sendRequestAsync(request);
    }

    function discardModifications(){
        viewEvent(currentEvent);
    }

    function markAsDone(){
        const payload = {
            month: calendarController.getCurrentDate().toString(),
            day: dailyTasksController.getCurrentDate().toString()
        }

        const request = new Request(Mapping.getEndpoint("DIARY_OCCURRENCE_DONE", {occurrenceId: currentEvent.occurrenceId}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(days){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                currentEvent.status = "DONE";
                viewEvent(currentEvent);
            }
        dao.sendRequestAsync(request);
    }

    function markAsDefault(){
        const payload = {
            month: calendarController.getCurrentDate().toString(),
            day: dailyTasksController.getCurrentDate().toString()
        }

        const request = new Request(Mapping.getEndpoint("DIARY_OCCURRENCE_DEFAULT", {occurrenceId: currentEvent.occurrenceId}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(day){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                currentEvent.status = "PENDING";
                viewEvent(currentEvent);
            }
        dao.sendRequestAsync(request);
    }

    function markAsSnoozed(){
        const payload = {
            month: calendarController.getCurrentDate().toString(),
            day: dailyTasksController.getCurrentDate().toString()
        }

        const request = new Request(Mapping.getEndpoint("DIARY_OCCURRENCE_SNOOZED", {occurrenceId: currentEvent.occurrenceId}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(day){
                eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                currentEvent.status = "SNOOZED";
                viewEvent(currentEvent);
            }
        dao.sendRequestAsync(request);
    }

    function deleteEvent(){
        const payload = {
            month: calendarController.getCurrentDate().toString(),
            day: dailyTasksController.getCurrentDate().toString()
        }

        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-event-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("delete-event-confirmation-dialog-detail", {title: currentEvent.title}))
            .withConfirmButton(Localization.getAdditionalContent("delete-event-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-event-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-event-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("DIARY_EVENT_DELETE", {eventId: currentEvent.eventId}), payload);
                    request.convertResponse = jsonConverter;
                    request.processValidResponse = function(days){
                        notificationService.showSuccess(Localization.getAdditionalContent("event-deleted"));
                        eventProcessor.processEvent(new Event(events.EVENT_CHANGED, days));
                        pageController.displayMainPage();
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
})();