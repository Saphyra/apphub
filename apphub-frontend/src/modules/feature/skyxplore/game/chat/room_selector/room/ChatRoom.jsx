import LocalizationHandler from "common/js/LocalizationHandler";
import ChatConstants from "../../ChatConstants";
import localizationData from "./chat_room_localization.json";
import "./chat_room.css";

const DEFAULT_CHAT_ROOMS = [
    ChatConstants.GENERAL_CHAT_ROOM,
    ChatConstants.ALLIANCE_CHAT_ROOM,
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
        <span>{localizationHandler.getOrDefault(roomId, roomTitle)}</span>

        {isCustomRoom() && getCloseButton()}
    </div>
}

export default ChatRoom;