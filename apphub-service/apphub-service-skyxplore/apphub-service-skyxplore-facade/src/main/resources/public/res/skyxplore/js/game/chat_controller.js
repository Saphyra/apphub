(function ChatController(){
    const ROOM_GENERAL = "general";
    const ROOM_ALLIANCE = "alliance";

    let activeRoom = null;

    $(document).ready(init);

    window.chatController = new function(){
        this.openCreateChatRoomDialog = openCreateChatRoomDialog;
        this.createChatRoom = createChatRoom;

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

        this.createChatRoomCreatedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return webSocketEvents.CHAT_ROOM_CREATED == eventName},
                processChatRoomCreatedEvent
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

        if(room != activeRoom){
            document.getElementById(createNotificationId(room)).style.display = "inline";
        }

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

    function openCreateChatRoomDialog(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_GET_PLAYERS"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(players){
                displayCreateChatRoomDialog(players);
            }
        dao.sendRequestAsync(request);

        function displayCreateChatRoomDialog(players){
            document.getElementById(ids.createChatRoomTitleInput).value = "";

            document.getElementById(ids.createChatRoomNoPlayers).style.display = players.length > 1 ? "none" : "block";

            const playersContainer = document.getElementById(ids.createChatRoomPlayerList);
                playersContainer.innerHTML = "";

                new Stream(players)
                    .filter(function(player){return player.id != window.userId})
                    .map(createNode)
                    .forEach(function(node){playersContainer.appendChild(node)});

            document.getElementById(ids.createChatRoomContainer).style.display = "block";

            function createNode(player){
                const container = document.createElement("LABEL");
                    container.classList.add("create-chat-room-player");
                    container.classList.add("button");

                    const checkbox = document.createElement("INPUT");
                        checkbox.classList.add("create-chat-room-player-checkbox");
                        checkbox.value = player.id;
                        checkbox.type = "checkbox";
                container.appendChild(checkbox);

                    const playerName = document.createTextNode(player.name);
                container.appendChild(playerName);

                return container;
            }
        }
    }

    function createChatRoom(){
        const title = document.getElementById(ids.createChatRoomTitleInput).value;
        if(!title.length){
            notificationService.showError(Localization.getAdditionalContent("chat-room-title-empty"));
            return
        }

        if(title.length > 20){
            notificationService.showError(Localization.getAdditionalContent("chat-room-title-too-long"));
            return
        }

        const members = new Stream(document.getElementsByClassName("create-chat-room-player-checkbox"))
            .filter(function(checkbox){return checkbox.checked})
            .map(function(checkbox){return checkbox.value})
            .toList();

        const payload = {
            roomTitle: title,
            members: members
        };

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_CREATE_CHAT_ROOM"), payload);
            request.processValidResponse = function(){
                document.getElementById(ids.createChatRoomContainer).style.display = "none";
            }
        dao.sendRequestAsync(request);
    }

    function processChatRoomCreatedEvent(chatRoom){
        const chatRoomButton = document.createElement("SPAN");
            chatRoomButton.classList.add("button");
            chatRoomButton.classList.add("chat-button");
            chatRoomButton.id = createChatButtonId(chatRoom.id);

            const title = document.createElement("SPAN");
                title.innerText = chatRoom.title;
        chatRoomButton.appendChild(title);

            const notification = document.createElement("SPAN");
                notification.classList.add("chat-notification");
                notification.id = createNotificationId(chatRoom.id);
                notification.innerHTML = " (!)";
        chatRoomButton.appendChild(notification);

            const leaveButton = document.createElement("SPAN");
                leaveButton.classList.add("chat-leave-button");
                leaveButton.classList.add("button");
                leaveButton.innerHTML = "X";
        chatRoomButton.appendChild(leaveButton);

        document.getElementById(ids.chatRooms).appendChild(chatRoomButton);

        const messageContainer = document.createElement("DIV");
            messageContainer.classList.add("chat-message-container");
            messageContainer.id = createChatMessageContainerId(chatRoom.id);
        document.getElementById(ids.chatMessageContainers).appendChild(messageContainer);

        chatRoomButton.onclick = function(){
            selectRoom(chatRoom.id);
        }

        leaveButton.onclick = function(e){
            e.stopPropagation();
            const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_LEAVE_ROOM", {roomId: chatRoom.id}));
                request.processValidResponse = function(){
                    document.getElementById(ids.chatRooms).removeChild(chatRoomButton);
                    document.getElementById(ids.chatMessageContainers).removeChild(messageContainer);
                    if(activeRoom == chatRoom.id){
                        selectRoom(ROOM_GENERAL);
                    }
                }
            dao.sendRequestAsync(request);
        }
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

    function selectRoom(room){
        activeRoom = room;
        $(".chat-button").removeClass("active-room-button");
        $("#" + createChatButtonId(room)).addClass("active-room-button");

        switchTab("chat-message-container", createChatMessageContainerId(room));

        document.getElementById(createNotificationId(room)).style.display = "none";
    }

    function createChatButtonId(room){
        return room + "-chat-button";
    }

    function createChatMessageContainerId(room){
        return room + "-chat-message-container";
    }

    function createNotificationId(room){
        return room + "-chat-notification";
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
            selectRoom(ROOM_GENERAL);
        }

        document.getElementById(ids.allianceChatButton).onclick = function(){
            selectRoom(ROOM_ALLIANCE);
        }

        selectRoom(ROOM_GENERAL);
    }
})();