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

const Chat = ({ localizationHandler, ownUserId, lastEvent, sendWsMessage }) => {
    const [messages, setMessages] = useState([]);

    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE) {
            const message = lastEvent.payload;
            message.type = message.senderId === ownUserId ? MessageType.OWN_MESSAGE : MessageType.INCOMING_MESSAGE;

            const copy = new Stream(messages)
                .add(message)
                .toList();

            setMessages(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_EXIT) {
            if (lastEvent.payload.onlyInvited) {
                return;
            }

            const message = {
                type: MessageType.SYSTEM_MESSAGE,
                message: localizationHandler.get("player-left-lobby", { name: lastEvent.payload.characterName }),
                createdAt: lastEvent.payload.createdAt
            }

            const copy = new Stream(messages)
                .add(message)
                .toList();

            setMessages(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED) {
            const message = {
                type: MessageType.SYSTEM_MESSAGE,
                message: localizationHandler.get("player-connected", { name: lastEvent.payload.characterName }),
                createdAt: lastEvent.payload.createdAt
            }

            const copy = new Stream(messages)
                .add(message)
                .toList();

            setMessages(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED) {
            const message = {
                type: MessageType.SYSTEM_MESSAGE,
                message: localizationHandler.get("player-disconnected", { name: lastEvent.payload.characterName }),
                createdAt: lastEvent.payload.createdAt
            }

            const copy = new Stream(messages)
                .add(message)
                .toList();

            setMessages(copy);
        }
    }

    const onkeyup = (event) => {
        if (event.which === 13) {
            if(sendMessage(event.target.value)){
                event.target.value = "";
            }
        }
    }

    const sendMessage = (message) => {
        if(message.length > Constants.SKYXPLORE_MAX_CHAT_MESSAGE_SIZE){
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
                />
            </div>

            <div id="skyxplore-lobby-chat-content">
                {getMessages()}
            </div>
        </div>
    );
}

export default Chat;