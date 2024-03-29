(function NotificationService(){
    scriptLoader.loadScript("/res/common/js/animation/roll.js");

    window.notificationService = new function(){
        this.showSuccess = showSuccess;
        this.showError = showError;
        this.showMessage = showMessage;
        this.storeError = (errorMessage) => sessionStorage.errorMessage = errorMessage;
        this.storeSuccess = (successMessage) => sessionStorage.successMessage = successMessage;
        this.storeErrorText = (errorText) => sessionStorage.errorText = errorText;
        this.storeSuccessText = (successText) => sessionStorage.successText = successText;
        this.printStoredMessages = printStoredMessages;
    }

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
            
            roll.rollInHorizontal(messageElement, container, "inline-block", 300)
            .then(() => new Promise((resolve, reject) => {
                    setTimeout(function(){resolve();}, 10000)
                })
            )
            .then(() => roll.rollOutHorizontal(messageElement, 300))
            .then(() => setTimeout(() => container.removeChild(messageElement), 10000));

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
                showError(localization.getAdditionalContent(sessionStorage.errorMessage));
            } finally{
                delete sessionStorage.errorMessage;
            }
        }
        
        if(hasValue(sessionStorage.successMessage)){
            try{
                showSuccess(localization.getAdditionalContent(sessionStorage.successMessage));
            }finally{
                delete sessionStorage.successMessage;
            }
        }

        if(hasValue(sessionStorage.errorText)){
            try{
                showError(sessionStorage.errorText);
            }finally{
                delete sessionStorage.errorText;
            }
        }

        if(hasValue(sessionStorage.successText)){
            try{
                showSuccess(sessionStorage.successText);
            } finally{
                delete sessionStorage.successText;
            }
        }
    }
})();