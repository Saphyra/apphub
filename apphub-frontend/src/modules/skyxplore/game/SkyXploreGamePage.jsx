import React, { useEffect, useState } from "react";
import localizationData from "./skyxplore_game_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Redirection from "../Redirection";
import useWebSocket from "react-use-websocket";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Footer from "../../../common/component/Footer";
import PauseAndResumeGameButton from "./pause_and_resume/PauseAndResumeGameButton";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import ExitGameButton from "./exit_game/ExitGameButton";
import ToggleChatButton from "./chat/toggle_button/ToggleChatButton";
import Chat from "./chat/Chat";
import Endpoints from "../../../common/js/dao/dao";
import { ToastContainer } from "react-toastify";
import Navigation from "./navigation/Navigation";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName";

const SkyXploreGamePage = () => {
    //===Platform
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    useEffect(() => Redirection.forGame(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [userId, setUserId] = useState("");

    useEffect(() => fetchUserId(), []);

    const fetchUserId = () => {
        const fetch = async () => {
            const response = await Endpoints.GET_OWN_USER_ID.createRequest()
                .send();
            setUserId(response.value);
        }
        fetch();
    }
    //===End Platform

    //===WebSocket
    const webSocketUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_GAME_MAIN;
    const [lastEvent, setLastEvent] = useState(null);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => true,
        }
    );
    useEffect(() => handleMessage(), [lastMessage]);

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
    //===End WebSocket

    //===Chat
    const [hasUnreadMessage, setHasUnreadMessage] = useState(false);
    const [displayChat, setDisplayChat] = useState(false);
    //===End Chat

    const footer = () => {
        return <Footer
            leftButtons={[
                <ExitGameButton
                    key="exit"
                    setConfirmationDialogData={setConfirmationDialogData}
                />,
                <PauseAndResumeGameButton
                    key="pause-and-resume"
                    lastEvent={lastEvent}
                />
            ]}
            centerButtons={[]}
            rightButtons={[
                <ToggleChatButton
                    key="toggle-chat"
                    hasUnreadMessage={hasUnreadMessage}
                    toggleCallback={() => setDisplayChat(!displayChat)}
                />
            ]}
        />
    }

    return (
        <div>
            <div className="skyxplore-background">
                <div id="skyxplore-game" className="main-page skyxplore-main" >
                    <Navigation
                        footer={footer()}
                        setConfirmationDialogData={setConfirmationDialogData}
                    />
                </div>
            </div>

            <Chat
                displayChat={displayChat}
                setHasUnreadMessage={setHasUnreadMessage}
                lastEvent={lastEvent}
                userId={userId}
                sendMessage={sendMessage}
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            <ToastContainer />
        </div>
    )
}

export default SkyXploreGamePage