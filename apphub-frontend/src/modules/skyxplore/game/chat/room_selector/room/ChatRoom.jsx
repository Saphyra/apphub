import React from "react";
import Button from "../../../../../../common/component/input/Button";
import localizationData from "./chat_room_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./chat_room.css";

const ChatRoom = ({ roomId, hasUnread, currentChatRoom, setCurrentChatRoom }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return <Button
        id={"skyxplore-game-chat-room-" + roomId}
        className={"skyxplore-game-chat-room" + (hasUnread ? " unread" : "") + (roomId === currentChatRoom ? " current" : "")}
        label={localizationHandler.getOrDefault(roomId, roomId)}
        onclick={() => setCurrentChatRoom(roomId)}
    />
}

export default ChatRoom;