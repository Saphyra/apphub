import React from "react";
import MessageType from "./MessageType";

const Message = ({ message }) => {
    const header = () => {
        return <legend className="skyxplore-lobby-message-from">{message.senderName}</legend>
    }

    const content = () => {
        return (
            <div className="skyxplore-lobby-message-content">
                {message.message}
            </div>
        );
    }

    return (
        <div className={"skyxplore-lobby-message-wrapper " + message.type}>
            <fieldset className={"skyxplore-lobby-message " + message.type}>
                {message.type !== MessageType.SYSTEM_MESSAGE && header()}
                {content()}
            </fieldset>
        </div>
    );
}

export default Message;