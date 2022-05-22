scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function CalendarController(){
    pageLoader.addLoader(loadCalendar, "Loading current month to calendar");

    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.calendar)
        .withGetKeyMethod((day) => {return day.date})
        .withCreateNodeMethod(createDayNode)
        .withSortMethod((a, b) => {return a.date.localeCompare(b.date)})
        .withIdPrefix("calendar-day")
        .build();

    let rowAmount;
    let currentDate;

    window.calendarController = new function(){
        this.previousMonth = function(){
            loadCalendar(currentDate.minusMonths(1));
        }
        this.nextMonth = function(){
            loadCalendar(currentDate.plusMonths(1));
        }
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
        dao.sendRequestAsync(request);
    }

    function createDayNode(day){
        const node = document.createElement("DIV");
            node.classList.add("calendar-day");
            node.classList.add("button");

            node.style.height = "calc((100vh - 14rem) / " + rowAmount + ")";

            if(extractMonth(day.date) != currentDate.getMonth()){
                node.classList.add("different-month-day");
            }

            if(day.date == CURRENT_DATE.toString()){
                node.classList.add("current-day");
            }

            const title  = document.createElement("DIV");
                title.classList.add("calendar-day-title");
                title.innerText = extractDay(day.date)
        node.appendChild(title);

        node.onclick = function(){
            dailyTasksController.displayDay(parseLocalDate(day.date));
            $(".calendar-day").removeClass("selected-day");
            node.classList.add("selected-day");
        }

        return node;
    }
})();