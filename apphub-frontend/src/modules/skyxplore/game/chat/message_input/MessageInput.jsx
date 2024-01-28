import React, { useState } from "react";
import "./message_input.css";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./message_input_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";

const MessageInput = ({ sendMessage, currentChatRoom }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [message, setMessage] = useState("");

    const sendIfEnter = (e) => {
        if (e.which === 13) {
            const event = {
                eventName: WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE,
                payload: {
                    room: currentChatRoom,
                    message: message
                }
            }

            sendMessage(JSON.stringify(event));
            setMessage("");
        }
    }

    return (
        <div id="skyxplore-game-chat-message-input-container">
            <InputField
                type="text"
                id="skyxplore-game-chat-message-input"
                onchangeCallback={setMessage}
                onkeyupCallback={sendIfEnter}
                placeholder={localizationHandler.get("placeholder")}
                value={message}
            />
        </div>
    );
}

export default MessageInput;