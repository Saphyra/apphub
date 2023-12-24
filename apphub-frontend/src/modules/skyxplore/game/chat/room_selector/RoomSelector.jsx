import React, { useEffect, useState } from "react";
import "./room_selector.css";
import Endpoints from "../../../../../common/js/dao/dao";
import Stream from "../../../../../common/js/collection/Stream";
import ChatRoom from "./room/ChatRoom";
import Utils from "../../../../../common/js/Utils";
import WebSocketEventName from "../../../../../common/js/ws/WebSocketEventName";

const RoomSelector = ({ currentChatRoom, setCurrentChatRoom, unreadMessages, setDisplayGroupCreator, lastEvent }) => {
    const [chatRooms, setChatRooms] = useState([]);
    useEffect(() => loadChatRooms(), []);
    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (!Utils.hasValue(lastEvent)) {
            return;
        }

        if (lastEvent.eventName == WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED) {
            Utils.addAndSet(chatRooms, lastEvent.payload, setChatRooms);
        }
    }

    const loadChatRooms = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GAME_GET_CHAT_ROOMS.createRequest()
                .send();
            setChatRooms(response);
        }
        fetch();
    }

    const exitChatRoom = async (roomId) => {
        await Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM.createRequest(null, { roomId: roomId })
            .send();

        Utils.removeAndSet(chatRooms, (chatRoom) => chatRoom.roomId === roomId, setChatRooms);
    }

    const getChatRooms = () => {
        return new Stream(chatRooms)
            .map(chatRoom => <ChatRoom
                key={chatRoom.roomId}
                roomId={chatRoom.roomId}
                roomTitle={chatRoom.roomTitle}
                hasUnread={unreadMessages[chatRoom.roomId]}
                currentChatRoom={currentChatRoom}
                setCurrentChatRoom={setCurrentChatRoom}
                exitChatRoom={exitChatRoom}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-chat-room-selector">
            {getChatRooms()}

            <div
                id="skyxplore-game-chat-room-create-button"
                className="button"
                onClick={() => setDisplayGroupCreator(true)}
            >
                +
            </div>
        </div>
    );
}

export default RoomSelector;