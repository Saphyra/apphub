import React from "react";
import WebSocketEventName from "../../../../../../common/js/ws/WebSocketEventName";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import localizationData from "./message_localization.json";
import "./message.css";

const Message = ({ message, userId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    if (message.eventName === WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE) {
        const messageData = message.payload;

        return (
            <div className="skyxplore-game-chat-message">
                <span className={"skyxplore-game-chat-message-sender" + (userId === messageData.senderId ? " own-message" : "")}>{messageData.senderName}</span>
                <span>: </span>
                {messageData.message}
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
        <div className="skyxplore-game-chat-message system-message">
            {localizationHandler.get(messageType, { name: messageData.characterName })}
        </div>
    );
}

export default Message;