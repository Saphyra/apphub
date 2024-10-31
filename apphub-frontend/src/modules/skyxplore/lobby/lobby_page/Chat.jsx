import React, { useEffect, useState } from "react";
import PanelTitle from "./PanelTitle";
import InputField from "../../../../common/component/input/InputField";
import "./chat/chat.css";
import Message from "./chat/Message";
import Stream from "../../../../common/js/collection/Stream";
import MessageType from "./chat/MessageType";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";
import Constants from "../../../../common/js/Constants";
import NotificationService from "../../../../common/js/notification/NotificationService";

const Chat = ({ localizationHandler, sendWsMessage, messages, sendMessages }) => {
    const [message, setMessage] = useState("");

    const onkeyup = (event) => {
        if (event.which === 13) {
            if (sendMessage(message)) {
                setMessage("");
            }
        }
    }

    const sendMessage = (message) => {
        if (message.length > Constants.SKYXPLORE_MAX_CHAT_MESSAGE_SIZE) {
            NotificationService.showError(localizationHandler.get("chat-message-too-long"));
            return false;
        }

        const event = {
            eventName: WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE,
            payload: message
        }

        sendWsMessage(JSON.stringify(event));
        return true;
    }

    const getMessages = () => {
        return new Stream(messages)
            .sorted((a, b) => b.createdAt - a.createdAt)
            .map(message =>
                <Message
                    key={message.createdAt}
                    message={message}
                />
            )
            .toList();
    }

    return (
        <div id="skyxplore-lobby-chat" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("chat")} />

            <div id="skyxplore-lobby-chat-input-container">
                <InputField
                    id="skyxplore-lobby-chat-input"
                    placeholder={localizationHandler.get("message")}
                    onkeyupCallback={onkeyup}
                    onchangeCallback={setMessage}
                    value={message}
                />
            </div>

            <div id="skyxplore-lobby-chat-content">
                {getMessages()}
            </div>
        </div>
    );
}

export default Chat;