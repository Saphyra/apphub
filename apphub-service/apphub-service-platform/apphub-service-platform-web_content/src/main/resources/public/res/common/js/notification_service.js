(function NotificationService(){
    scriptLoader.loadScript("/res/common/js/animation/animationfacade.js");

    window.notificationService = new function(){
        this.showSuccess = showSuccess;
        this.showError = showError;
        this.showMessage = showMessage;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType === events.LOCALIZATION_LOADED},
        printStoredMessages,
        true
    ));
    /*
    Shows a success message.
    Arguments:
        - message: the text to show.
    */
    function showSuccess(message){
        showMessage(message, "notification-success");
    }
    
    /*
    Shows an error message.
    Arguments:
        - message: the text to show.
    */
    function showError(message){
        showMessage(message, "notification-error");
    }
    
    /*
    Shows a notification message with the given color.
    Arguments:
        - message: the text to show.
        - bgColor: the background color of the notification.
    */
    function showMessage(message, messageClass){
        const container = document.getElementById("notification-container") || createContainer();
            const messageElement = createMessageElement(message, messageClass);
            messageElement.onclick = function(){
                container.removeChild(messageElement);
            }
            
            animationFacade.rollInHorizontal(messageElement, container, 300)
            .then(() => new Promise((resolve, reject) => {
                    setTimeout(function(){resolve();}, 10000)
                })
            )
            .then(() => animationFacade.rollOutHorizontal(messageElement, 300))
            .then(() => setTimeout(function(){container.removeChild(messageElement)}, 10000));

        function createContainer(){
            const container = document.createElement("DIV");
                container.id = "notification-container";
            document.body.appendChild(container);
            return container;
        }

        function createMessageElement(message, messageClass){
            const wrapper = document.createElement("DIV");
                wrapper.classList.add("notification-message-wrapper")

                const element = document.createElement("DIV");
                    element.classList.add("notification-message");
                    element.classList.add(messageClass);
                    element.innerText = message;
                    element.classList.add("button");
            wrapper.appendChild(element);
            return wrapper;
        }
    }

    /*
    Shows the messages stored in sessionStorage.
    */
    function printStoredMessages(){
        if(hasValue(sessionStorage.errorMessage)){
            try{
                showError(Localization.getAdditionalContent(sessionStorage.errorMessage));
            } finally{
                delete sessionStorage.errorMessage;
            }
        }
        
        if(hasValue(sessionStorage.successMessage)){
            try{
                showSuccess(Localization.getAdditionalContent(sessionStorage.successMessage));
            }finally{
                delete sessionStorage.successMessage;
            }
        }
    }
})();