(function TextViewController(){
    let openedTextId = null;

    window.textViewController = new function(){
        this.viewText = viewText;
        this.enableEditing = enableEditing;
        this.saveChanges = saveChanges;
        this.discardChanges = discardChanges;
    }

    function viewText(textId){
        openedTextId = textId;
        const request = new Request(Mapping.getEndpoint("GET_NOTEBOOK_TEXT", {textId: textId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displayText;
        dao.sendRequestAsync(request);
    }

    function displayText(textData){
        const titleNode = document.getElementById("view-text-title");
        titleNode.innerHTML = textData.title;
        titleNode.contentEditable = false;

        const contentArea = document.getElementById("view-text-content");
        contentArea.value = textData.content;
        contentArea.readOnly = true;
        switchTab("main-page", "view-text");
        switchTab("view-text-button-wrapper", "view-text-edit-button-wrapper");
    }

    function enableEditing(){
        document.getElementById("view-text-content").readOnly = false;
        document.getElementById("view-text-title").contentEditable = true;
        switchTab("view-text-button-wrapper", "view-text-editing-operations-button-wrapper");
    }

    function saveChanges(){
        //TODO implement
    }

    function discardChanges(){
        textViewController.viewText(openedTextId);
    }
})();