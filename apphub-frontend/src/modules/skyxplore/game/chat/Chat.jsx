import React, { useEffect, useState } from "react";
import "./chat.css";
import RoomSelector from "./room_selector/RoomSelector";
import Messages from "./messages/Messages";
import MessageInput from "./message_input/MessageInput";
import GameConstants from "../GameConstants";
import Utils from "../../../../common/js/Utils";
import WebSocketEventName from "../../../../common/js/ws/WebSocketEventName";
import MapStream from "../../../../common/js/collection/MapStream";

const CHAT_EVENTS = [
    WebSocketEventName.SKYXPLORE_GAME_USER_JOINED,
];

const Chat = ({
    displayChat,
    setHasUnreadMessage,
    lastEvent
}) => {
    const [currentChatRoom, setCurrentChatRoom] = useState(GameConstants.GENERAL_CHAT_ROOM);
    const [messages, setMessages] = useState({});
    const [unreadMessages, setUnreadMessages] = useState({});

    useEffect(() => handleEvent(), [lastEvent]);
    useEffect(() => updateUnreadMessage(), [messages]);
    useEffect(() => updateHasUnreadMessage(), [unreadMessages]);
    useEffect(() => setMessageStatus(currentChatRoom, false), [currentChatRoom]);

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
        return (
            <div id="skyxplore-game-chat">
                <RoomSelector
                    currentChatRoom={currentChatRoom}
                    setCurrentChatRoom={setCurrentChatRoom}
                    unreadMessages={unreadMessages}
                />

                <Messages

                />

                <MessageInput

                />
            </div>
        );
    }
}

export default Chat;