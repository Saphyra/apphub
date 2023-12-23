import React, { useEffect, useState } from "react";
import "./room_selector.css";
import Endpoints from "../../../../../common/js/dao/dao";
import Stream from "../../../../../common/js/collection/Stream";
import ChatRoom from "./room/ChatRoom";
import Button from "../../../../../common/component/input/Button";

const RoomSelector = ({ currentChatRoom, setCurrentChatRoom, unreadMessages }) => {
    const [chatRooms, setChatRooms] = useState([]);
    useEffect(() => loadChatRooms(), []);

    const loadChatRooms = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GAME_GET_CHAT_ROOMS.createRequest()
                .send();
            setChatRooms(response);
        }
        fetch();
    }

    const getChatRooms = () => {
        return new Stream(chatRooms)
            .map(roomId => <ChatRoom
                key={roomId}
                roomId={roomId}
                hasUnread={unreadMessages[roomId]}
                currentChatRoom={currentChatRoom}
                setCurrentChatRoom={setCurrentChatRoom}
            />)
            .toList();
    }

    const openCreateChatRoomDialog = () => {
        //TODO implement
    }

    return (
        <div id="skyxplore-game-chat-room-selector">
            {getChatRooms()}

            <Button
                id="skyxplore-game-chat-room-create-button"
                label="+"
                onclick={openCreateChatRoomDialog}
            />
        </div>
    );
}

export default RoomSelector;