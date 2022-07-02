(function ViewEventController(){
    window.viewEventController = new function(){
        this.viewEvent = viewEvent;
    }

    function viewEvent(event){
        const title = document.getElementById(ids.viewEventTitle);
            title.innerText = event.title;
            title.contentEditable = false;
        const contentNode = document.getElementById(ids.viewEventContent);
            contentNode.value = event.content;
            contentNode.disabled = true;
        const noteNode = document.getElementById(ids.viewEventNote)
            noteNode.value = event.note;
            noteNode.disabled = true;

        switchTab("main-page", ids.viewEventPage);
    }
})();