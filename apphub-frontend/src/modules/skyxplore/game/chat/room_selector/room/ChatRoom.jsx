import React from "react";
import Button from "../../../../../../common/component/input/Button";
import localizationData from "./chat_room_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./chat_room.css";
import GameConstants from "../../../GameConstants";

const DEFAULT_CHAT_ROOMS = [
    GameConstants.GENERAL_CHAT_ROOM,
    GameConstants.ALLIANCE_CHAT_ROOM,
];

const ChatRoom = ({ roomId, roomTitle, hasUnread, currentChatRoom, setCurrentChatRoom, exitChatRoom }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const isCustomRoom = () => {
        return DEFAULT_CHAT_ROOMS.indexOf(roomId) < 0;
    }

    const getCloseButton = () => {
        return (
            <span>
                <span> </span>

                <div
                    className="button skyxplore-game-chat-room-close-button inline"
                    label="X"
                    onClick={() => exitChatRoom(roomId)}
                >
                    X
                    </div>
            </span>
        )
    }

    return <div
        id={"skyxplore-game-chat-room-" + roomId}
        className={"button skyxplore-game-chat-room" + (hasUnread ? " unread" : "") + (roomId === currentChatRoom ? " current" : "") + (isCustomRoom() ? " custom" : "")}
        onClick={() => setCurrentChatRoom(roomId)}
    >
        {localizationHandler.getOrDefault(roomId, roomTitle)}

        {isCustomRoom() && getCloseButton()}
    </div>
}

export default ChatRoom;