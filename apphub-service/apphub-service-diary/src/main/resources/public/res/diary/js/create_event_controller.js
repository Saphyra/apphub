(function CreateEventController(){
    let currentDate;

    window.createEventController = new function(){
        this.createEvent = createEvent;
    }

    function createEvent(){
        currentDate = dailyTasksController.getCurrentDate();

        console.log(currentDate);
    }
})();