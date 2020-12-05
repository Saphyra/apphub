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
        const senderId = chatEvent.senderId;
        const senderName = chatEvent.senderName;
        const message = chatEvent.message;

        const messagesContainer = document.getElementById("messages");
            const senderContainer = getSenderContainer(messagesContainer, senderId, senderName);

            const messageNode = document.createElement("DIV");
                messageNode.classList.add("chat-message");
                if(senderId != window.characterId){
                    messageNode.classList.add("not-own-message");
                }
                messageNode.innerHTML = message;

            getMessageList(senderContainer).appendChild(messageNode);


        function getSenderContainer(messagesContainer, senderId, senderName){
            const childNodes = messagesContainer.childNodes;
            if(childNodes.length && childNodes[childNodes.length - 1].getAttribute("sender-id") == senderId){
                return childNodes[childNodes.length - 1];
            }

            const newSenderContainer = createSenderContainer(senderId, senderName);
                messagesContainer.appendChild(newSenderContainer);
            return newSenderContainer;

            function createSenderContainer(senderId, senderName){
                const senderContainer = document.createElement("DIV");
                    senderContainer.classList.add("message-sender-container");
                    senderContainer.setAttribute("sender-id", senderId);
                    if(senderId == window.characterId){
                        senderContainer.classList.add("own-message");
                    }

                    const wrapper = document.createElement("DIV");
                        wrapper.classList.add("message-wrapper");

                        const senderNameNode = document.createElement("DIV");
                            senderNameNode.classList.add("sender-name");
                            senderNameNode.innerHTML = senderName;
                    wrapper.appendChild(senderNameNode);

                        const messageList = document.createElement("DIV");
                            if(senderId != window.characterId){
                                messageList.classList.add("not-own-message");
                            }

                            messageList.classList.add("message-list");
                    wrapper.appendChild(messageList);

                senderContainer.appendChild(wrapper);
                return senderContainer;
            }
        }

        function getMessageList(senderContainer){
            return senderContainer.childNodes[0].childNodes[1];
        }
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