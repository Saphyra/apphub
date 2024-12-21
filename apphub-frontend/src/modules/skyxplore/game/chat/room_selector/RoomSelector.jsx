import React, { useEffect } from "react";
import "./room_selector.css";
import Stream from "../../../../../common/js/collection/Stream";
import ChatRoom from "./room/ChatRoom";
import { removeAndSet } from "../../../../../common/js/Utils";
import { SKYXPLORE_GAME_GET_CHAT_ROOMS, SKYXPLORE_GAME_LEAVE_CHAT_ROOM } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const RoomSelector = ({ currentChatRoom, setCurrentChatRoom, unreadMessages, setDisplayRoomCreator, chatRooms, setChatRooms }) => {
    useEffect(() => loadChatRooms(), []);

    const loadChatRooms = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_GAME_GET_CHAT_ROOMS.createRequest()
                .send();
            setChatRooms(response);
        }
        fetch();
    }

    const exitChatRoom = async (roomId) => {
        await SKYXPLORE_GAME_LEAVE_CHAT_ROOM.createRequest(null, { roomId: roomId })
            .send();

        removeAndSet(chatRooms, (chatRoom) => chatRoom.roomId === roomId, setChatRooms);
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
                onClick={() => setDisplayRoomCreator(true)}
            >
                +
            </div>
        </div>
    );
}

export default RoomSelector;