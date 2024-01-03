import { useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import WebSocketEventName from "./WebSocketEventName";
import Utils from "../Utils";

const useConnectToWebSocket = (
    endpoint,
    afterConnectedCallback = () => { },
    eventHandler = () => { }
) => {
    const webSocketUrl = "ws://" + window.location.host + endpoint;
    const [lastEvent, setLastEvent] = useState(null);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => true,
        }
    );
    useEffect(() => handleMessage(), [lastMessage]);
    useEffect(() => afterConnected(), [sendMessage]);
    useEffect(() => handleEvent(), [lastEvent]);

    const handleMessage = () => {
        if (lastMessage === null) {
            return;
        }

        const message = JSON.parse(lastMessage.data);

        if (message.eventName === WebSocketEventName.PING) {
            sendMessage(lastMessage.data);
        }

        setLastEvent(message);
    }

    const afterConnected = () => {
        if (!Utils.hasValue(sendMessage)) {
            return;
        }

        afterConnectedCallback(sendMessage);
    }

    const handleEvent = () => {
        if (!Utils.hasValue(lastEvent)) {
            return;
        }

        eventHandler(lastEvent);
    }

    return {
        lastEvent: lastEvent,
        sendMessage: sendMessage
    }
}

export default useConnectToWebSocket;