(function ChatController(){
    const ROOM_GENERAL = "general";
    const ROOM_ALLIANCE = "alliance";

    let activeRoom = null;

    $(document).ready(init);

    window.chatController = new function(){
        this.createRoom = createRoom;

        this.createChatSendMessageHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return webSocketEvents.CHAT_SEND_MESSAGE == eventName},
                processMessageSentEvent
            );
        }

        this.createUserJoinedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return webSocketEvents.USER_JOINED == eventName},
                processUserJoinedEvent
            );
        }

        this.createUserLeftHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return webSocketEvents.USER_LEFT == eventName},
                processUserLeftEvent
            );
        }
    }

    function processMessageSentEvent(chatEvent){
        const senderId = chatEvent.senderId;
        const senderName = chatEvent.senderName;
        const message = chatEvent.message;
        const room = chatEvent.room;

        const messagesContainer = document.getElementById(createChatMessageContainerId(room));
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
                            senderNameNode.innerHTML = senderName;
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

    function processUserJoinedEvent(event){
        const characterName = event.characterName;
        const room = event.room;

        const messagesContainer = document.getElementById(createChatMessageContainerId(room));
            const joinMessageNode = document.createElement("DIV");
                joinMessageNode.classList.add("system-message");
                joinMessageNode.innerHTML = characterName + " " + Localization.getAdditionalContent("character-joined-to-game");
        messagesContainer.appendChild(joinMessageNode);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function processUserLeftEvent(event){
        const characterName = event.characterName;
        const room = event.room;

        const messagesContainer = document.getElementById(createChatMessageContainerId(room));
            const joinMessageNode = document.createElement("DIV");
                joinMessageNode.classList.add("system-message");
                joinMessageNode.innerHTML = characterName + " " + Localization.getAdditionalContent("character-left-the-game");
        messagesContainer.appendChild(joinMessageNode);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function createRoom(){
        //TODO
    }

    function sendMessage(){
        const inputField = document.getElementById(ids.chatMessageInput);
        const message = inputField.value;

        if(!message.length){
            return;
        }

        const payload = {
            room: activeRoom,
            message: message
        };

        const event = new WebSocketEvent(webSocketEvents.CHAT_SEND_MESSAGE, payload);

        pageController.webSocketConnection.sendEvent(event);

        inputField.value = "";
    }

    function selectChannel(room){
        activeRoom = room;
        $(".chat-button").removeClass("active-room-button");
        $("#" + createChatButtonId(room)).addClass("active-room-button");

        switchTab("chat-message-container", createChatMessageContainerId(room));
    }

    function createChatButtonId(room){
        return room + "-chat-button";
    }

    function createChatMessageContainerId(room){
        return room + "-chat-message-container";
    }

    function init(){
        document.getElementById(ids.chatButton).onclick = function(){
            $("#" + ids.chatContainer).fadeToggle();
        }

        document.getElementById(ids.chatMessageInput).onkeyup = function(e){
            if(e.which == 13){
                sendMessage();
            }
        }

        document.getElementById(ids.generalChatButton).onclick = function(){
            selectChannel(ROOM_GENERAL);
        }

        document.getElementById(ids.allianceChatButton).onclick = function(){
            selectChannel(ROOM_ALLIANCE);
        }

        selectChannel(ROOM_GENERAL);
    }
})();