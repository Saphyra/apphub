import React, { useEffect, useState } from "react";
import localizationData from "./skyxplore_game_page_localization.json";
import WebSocketEndpoint from "../../../common/js/ws/WebSocketEndpoint";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Redirection from "../Redirection";
import useWebSocket from "react-use-websocket";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import WebSocketEventName from "../../../common/js/ws/WebSocketEventName";
import Footer from "../../../common/component/Footer";
import PauseAndResumeGameButton from "./pause_and_resume/PauseAndResumeGameButton";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import ExitGameButton from "./exit_game/ExitGameButton";
import ToggleChatButton from "./chat/toggle_button/ToggleChatButton";
import Chat from "./chat/Chat";

const SkyXploreGamePage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    useEffect(() => Redirection.forGame(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

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

    return (
        <div>
            <div className="skyxplore-background">
                <div id="skyxplore-game" className="main-page skyxplore-main" >
                    <Footer
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
                </div>
            </div>

            <Chat
                displayChat={displayChat}
                setHasUnreadMessage={setHasUnreadMessage}
                lastEvent={lastEvent}
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }
        </div>
    )
}

export default SkyXploreGamePage