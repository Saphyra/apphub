import React from "react";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import localizationData from "./message_localization.json";
import "./message.css";
import WebSocketEventName from "../../../../../../common/hook/ws/WebSocketEventName";

const Message = ({ message, userId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (message.eventName === WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE) {
        const messageData = message.payload;

        return (
            <div className="skyxplore-game-chat-message">
                <span className={"skyxplore-game-chat-message-sender" + (userId === messageData.senderId ? " own-message" : "")}>{messageData.senderName}</span>
                <span>: </span>
                <span className="skyxplore-game-chat-message-content">{messageData.message}</span>
            </div>
        );
    } else {
        return (
            <SystemMessage
                messageType={message.eventName}
                messageData={message.payload}
                localizationHandler={localizationHandler}
            />
        );
    }
}

const SystemMessage = ({ messageType, messageData, localizationHandler }) => {
    return (
        <div className="skyxplore-game-chat-message">
            <span className="skyxplore-game-chat-message-sender system-message">{localizationHandler.get("system")}</span>
            <span>: </span>
            <span className="skyxplore-game-chat-message-content">
                {localizationHandler.get(messageType, { name: messageData.characterName })}
            </span>
        </div>
    );
}

export default Message;