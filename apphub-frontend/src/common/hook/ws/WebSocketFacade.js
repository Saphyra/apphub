import { useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import WebSocketEventName from "./WebSocketEventName";
import { hasValue } from "../../js/Utils";
import { GET_WEB_SOCKET_PROTOCOL } from "../../js/dao/endpoints/GenericEndpoints";

const useConnectToWebSocket = (
    endpoint,
    eventHandler = () => { },
    afterConnectedCallback = () => { }
) => {
    const [protocol, setProtocol] = useState(null);

    const webSocketUrl = protocol + "://" + window.location.host + endpoint;
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => true,
        },
        hasValue(protocol)
    );
    useEffect(() => handleMessage(), [lastMessage]);
    useEffect(() => afterConnected(), [sendMessage]);
    useEffect(() => loadProtocol(), []);

    const loadProtocol = () => {
        const fetch = async () => {
            const response = await GET_WEB_SOCKET_PROTOCOL.createRequest()
                .send();

            setProtocol(response.value);
        }
        fetch();
    }

    const handleMessage = () => {
        if (!hasValue(lastMessage)) {
            return;
        }

        //{eventName, payload}
        const event = JSON.parse(lastMessage.data);

        if (event.eventName === WebSocketEventName.PING) {
            sendMessage(lastMessage.data);
            return;
        }

        eventHandler(event);
    }

    const afterConnected = () => {
        if (!hasValue(sendMessage)) {
            return;
        }

        afterConnectedCallback(sendMessage);
    }

    return {
        sendMessage: sendMessage
    }
}

export default useConnectToWebSocket;