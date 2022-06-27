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

        //TODO
    }

    function createTaskNode(event){
        //TODO
    }
})();