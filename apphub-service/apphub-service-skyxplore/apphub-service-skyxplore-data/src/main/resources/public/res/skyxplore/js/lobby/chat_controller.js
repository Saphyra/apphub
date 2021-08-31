(function ChatController(){
    pageLoader.addLoader(function(){
        document.getElementById("message-input").onkeyup = function(e){
            if(e.which == 13){
                sendMessage();
            }
        }
    }, "MessageInput event listener");

    window.chatController = new function(){
        this.createChatSendMessageHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "chat-send-message"},
                processChatSendMessageEvent
            );
        };

        this.createCharacterJoinedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "join-to-lobby"},
                processCharacterJoinedEvent
            );
        };

        this.createCharacterLeftHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "exit-from-lobby"},
                processCharacterLeftEvent
            );
        };
    }

    function processChatSendMessageEvent(chatEvent){
        const senderId = chatEvent.senderId;
        const senderName = chatEvent.senderName;
        const message = chatEvent.message;

        const messagesContainer = document.getElementById("messages");
            const senderContainer = getSenderContainer(messagesContainer, senderId, senderName);

            const messageNode = document.createElement("DIV");
                messageNode.classList.add("chat-message");
                if(senderId != window.userId){
                    messageNode.classList.add("not-own-message");
                }
                messageNode.innerText = message;

            getMessageList(senderContainer).appendChild(messageNode);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;


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
                    if(senderId == window.userId){
                        senderContainer.classList.add("own-message");
                    }else{
                        senderContainer.classList.add("not-own-message");
                    }

                    const wrapper = document.createElement("DIV");
                        wrapper.classList.add("message-wrapper");

                        const senderNameNode = document.createElement("DIV");
                            senderNameNode.classList.add("sender-name");
                            senderNameNode.innerText = senderName;
                    wrapper.appendChild(senderNameNode);

                        const messageList = document.createElement("DIV");
                            if(senderId != window.userId){
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

        if(!message.length){
            return;
        }

        const event = new WebSocketEvent(webSocketEvents.CHAT_SEND_MESSAGE, message);

        pageController.webSocketConnection.sendEvent(event);

        inputField.value = "";
    }

    function processCharacterJoinedEvent(event){
        const characterName = event.characterName;

        const messagesContainer = document.getElementById("messages");
            const joinMessageNode = document.createElement("DIV");
                joinMessageNode.classList.add("system-message");
                joinMessageNode.innerText = characterName + " " + Localization.getAdditionalContent("character-joined-to-lobby");
        messagesContainer.appendChild(joinMessageNode);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function processCharacterLeftEvent(event){
        const characterName = event.characterName;

        const messagesContainer = document.getElementById("messages");
            const joinMessageNode = document.createElement("DIV");
                joinMessageNode.classList.add("system-message");
                joinMessageNode.innerText = characterName + " " + Localization.getAdditionalContent("character-left-the-lobby");
        messagesContainer.appendChild(joinMessageNode);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
})();