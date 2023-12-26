import React, { useEffect, useState } from "react";
import "./chat.css";
import RoomSelector from "./room_selector/RoomSelector";
import Messages from "./messages/Messages";
import MessageInput from "./message_input/MessageInput";
import GameConstants from "../GameConstants";
import Utils from "../../../../common/js/Utils";
import WebSocketEventName from "../../../../common/js/ws/WebSocketEventName";
import MapStream from "../../../../common/js/collection/MapStream";
import ChatGroupCreator from "./group_creator/ChatGroupCreator";

const CHAT_EVENTS = [
    WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
    WebSocketEventName.SKYXPLORE_GAME_USER_LEFT,
    WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE,
];

const Chat = ({
    displayChat,
    setHasUnreadMessage,
    lastEvent,
    userId,
    sendMessage
}) => {
    const [currentChatRoom, setCurrentChatRoom] = useState(GameConstants.GENERAL_CHAT_ROOM);
    const [messages, setMessages] = useState({});
    const [unreadMessages, setUnreadMessages] = useState({});
    const [displayGroupCreator, setDisplayGroupCreator] = useState(false);

    useEffect(() => handleEvent(), [lastEvent]);
    useEffect(() => updateUnreadMessage(), [messages]);
    useEffect(() => updateHasUnreadMessage(), [unreadMessages]);
    useEffect(() => setMessageStatus(currentChatRoom, false), [currentChatRoom]);
    useEffect(() => setDisplayGroupCreator(false), [displayChat]);
    useEffect(() => setMessageStatus(currentChatRoom, false), [displayChat]);

    const handleEvent = () => {
        if (!Utils.hasValue(lastEvent)) {
            return;
        }

        if (CHAT_EVENTS.indexOf(lastEvent.eventName) > -1) {
            addMessage(lastEvent);
        }
    }

    const addMessage = (message) => {
        const roomId = message.payload.room;

        const room = messages[roomId] || [];

        room.push(message);

        messages[roomId] = room;

        const copy = new MapStream(messages)
            .add(roomId, room)
            .toObject();

        setMessages(copy);
    }

    const updateUnreadMessage = () => {
        if (!Utils.hasValue(lastEvent)) {
            return;
        }
        if (!displayChat || lastEvent.payload.room !== currentChatRoom) {
            setMessageStatus(lastEvent.payload.room, true);
        }
    }

    const updateHasUnreadMessage = () => {
        const value = new MapStream(unreadMessages)
            .toListStream()
            .anyMatch(unread => unread);
        setHasUnreadMessage(value);
    }

    const setMessageStatus = (roomId, status) => {
        const copy = new MapStream(unreadMessages)
            .add(roomId, status)
            .toObject();
        setUnreadMessages(copy);
    }

    if (displayChat) {
        if (!displayGroupCreator) {
            return (
                <div id="skyxplore-game-chat">
                    <MessageInput
                        sendMessage={sendMessage}
                        currentChatRoom={currentChatRoom}
                    />

                    <Messages
                        messages={messages[currentChatRoom]}
                        userId={userId}
                    />

                    <RoomSelector
                        currentChatRoom={currentChatRoom}
                        setCurrentChatRoom={setCurrentChatRoom}
                        unreadMessages={unreadMessages}
                        setDisplayGroupCreator={setDisplayGroupCreator}
                        lastEvent={lastEvent}
                    />
                </div>
            );
        } else {
            return <ChatGroupCreator
                setDisplayGroupCreator={setDisplayGroupCreator}
            />
        }
    }
}

export default Chat;