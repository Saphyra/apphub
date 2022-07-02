scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function DailyTasksController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.dailyTasks)
        .withGetKeyMethod((event) => {return event.occurrenceId})
        .withCreateNodeMethod(createTaskNode)
        .withSortMethod((a, b) => {return a.order - b.order})
        .withIdPrefix("task")
        .build();

    let currentDate = CURRENT_DATE;

    window.dailyTasksController = new function(){
        this.displayDay = loadDay;
        this.getCurrentDate = function(){
            return currentDate;
        }
    }

    function loadDay(day){
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
            node.title = event.content;

            node.onclick = function(e){
                viewEventController.viewEvent(event);
            }
        return node;
    }
})();