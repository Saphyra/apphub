scriptLoader.loadScript("/res/common/js/sync_engine.js");

(function DailyTasksController(){
    pageLoader.addLoader(loadDay, "Loading current day");

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

    function loadDay(date){
        currentDate = date || CURRENT_DATE;

        document.getElementById(ids.dailyTasksCurrentDay).innerText = currentDate.getYear() + " " + monthLocalization.get(currentDate.getMonth()) + " " + currentDate.getDay();
    }

    function createTaskNode(event){

    }
})();