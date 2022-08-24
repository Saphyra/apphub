scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function DailyTasksController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.dailyTasks)
        .withGetKeyMethod((event) => {return event.occurrenceId})
        .withCreateNodeMethod(createTaskNode)
        .withSortMethod((a, b) => {return occurrenceOrder.getOrder(a.status) - occurrenceOrder.getOrder(b.status)})
        .withIdPrefix("task")
        .build();

    let currentDate = CURRENT_DATE;

    eventProcessor.registerProcessor(new EventProcessor(
        (eventType) => {return eventType == events.EVENT_CHANGED},
        (event) => {
            new Stream(event.getPayload())
                .filter((day) => {return day.date == currentDate.toString()})
                .findFirst()
                .ifPresent((day) => displayDay(day));
        },
        false,
        "EventChanged EventProcessor for DailyTasksController"
    ));

    window.dailyTasksController = new function(){
        this.displayDay = displayDay;
        this.getCurrentDate = function(){
            return currentDate;
        }
    }

    function displayDay(day){
        console.log("Loading day", day);

        currentDate = LocalDate.parse(day.date) || CURRENT_DATE;

        document.getElementById(ids.dailyTasksCurrentDay).innerText = currentDate.getYear() + " " + monthLocalization.get(currentDate.getMonth()) + " " + currentDate.getDay();

        syncEngine.clear();
        syncEngine.addAll(day.events);
    }

    function createTaskNode(event){
        const node = document.createElement("DIV");
            node.classList.add("daily-task");
            node.classList.add("button");
            node.classList.add(event.status.toLowerCase());

            node.innerText = event.title;
            node.title = event.date + "\n" + event.content;

            node.onclick = function(e){
                viewEventController.viewEvent(event);
            }
        return node;
    }
})();