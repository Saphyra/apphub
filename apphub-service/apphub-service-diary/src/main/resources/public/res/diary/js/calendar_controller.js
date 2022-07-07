scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function CalendarController(){
    pageLoader.addLoader(loadCalendarFirstTime, "Loading current month to calendar");

    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.calendar)
        .withGetKeyMethod((day) => {return day.date})
        .withCreateNodeMethod(createDayNode)
        .withSortMethod((a, b) => {return a.date.localeCompare(b.date)})
        .withIdPrefix("calendar-day")
        .build();

    let rowAmount;
    let currentDate;

    eventProcessor.registerProcessor(new EventProcessor(
        (eventType) => {return eventType == events.EVENT_CHANGED},
        (event) => new Stream(event.getPayload()).forEach((day) => syncEngine.add(day)),
        false,
        "EventChanged EventProcessor for CalendarController"
    ));

    window.calendarController = new function(){
        this.previousMonth = function(){
            loadCalendar(currentDate.minusMonths(1));
        }
        this.nextMonth = function(){
            loadCalendar(currentDate.plusMonths(1));
        }
        this.getCurrentDate = function(){
            return currentDate;
        }
    }

    function loadCalendarFirstTime(){
        loadCalendar()
            .then(() => dailyTasksController.displayDay(syncEngine.get(currentDate.toString())));
    }

    function loadCalendar(date){
        currentDate = date || CURRENT_DATE;

        const request = new Request(Mapping.getEndpoint("DIARY_GET_CALENDAR", {}, {date: currentDate.toString()}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(days){
                rowAmount = days.length / 7;

                syncEngine.clear();
                syncEngine.addAll(days);

                document.getElementById(ids.calendarCurrentMonth).innerText = currentDate.getYear() + " " + monthLocalization.get(currentDate.getMonth());
            }
        return dao.sendRequestAsync(request);
    }

    function createDayNode(day){
        const date = LocalDate.parse(day.date);

        const node = document.createElement("DIV");
            node.classList.add("calendar-day");
            node.classList.add("button");

            node.style.height = "calc((100vh - 14rem) / " + rowAmount + ")";

            if(date.getMonth() != currentDate.getMonth()){
                node.classList.add("different-month-day");
            }

            if(date.equals(CURRENT_DATE)){
                node.classList.add("current-day");
            }

            const title  = document.createElement("DIV");
                title.classList.add("calendar-day-title");
                title.innerText = date.getDay();
        node.appendChild(title);

            const eventsWrapper = document.createElement("DIV");
                eventsWrapper.classList.add("calendar-event-wrapper");

                new Stream(day.events)
                    .map(createEvent)
                    .forEach((eventNode) => eventsWrapper.appendChild(eventNode));
        node.appendChild(eventsWrapper);

        node.onclick = function(){
            dailyTasksController.displayDay(day);
            $(".calendar-day").removeClass("selected-day");
            node.classList.add("selected-day");
        }

        return node;

        function createEvent(event){
            const node = document.createElement("DIV");
                node.classList.add("calendar-event");
                node.classList.add("button");
                node.classList.add(event.status.toLowerCase());
                node.innerText = event.title;
                node.title = event.content;

                node.onclick = function(e){
                    e.stopPropagation();
                    viewEventController.viewEvent(event);
                }
            return node;
        }
    }
})();