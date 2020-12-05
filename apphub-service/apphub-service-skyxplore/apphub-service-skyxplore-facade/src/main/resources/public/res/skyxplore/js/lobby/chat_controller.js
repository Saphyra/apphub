(function ChatController(){
    window.chatController = new function(){
        this.createHandler = createHandler;
    }

    $(document).ready(init);

    function createHandler(){
        return new WebSocketEventHandler(
            function(eventName){return eventName == "chat-send-message"},
            processChatEvent
        );
    }

    function processChatEvent(chatEvent){
        console.log(chatEvent);
    }

    function sendMessage(){
        const inputField = document.getElementById("message-input");
        const message = inputField.value;

        const event = new WebSocketEvent(webSocketEvents.CHAT_SEND_MESSAGE, message);

        pageController.webSocketConnection.sendEvent(event);

        inputField.value = "";
    }

    function init(){
        document.getElementById("message-input").onkeyup = function(e){
            if(e.which == 13){
                sendMessage();
            }
        }
    }
})();