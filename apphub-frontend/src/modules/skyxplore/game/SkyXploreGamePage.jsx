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

const SkyXploreGamePage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");
    const webSocketUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_GAME_MAIN;
    const [lastEvent, setLastEvent] = useState(null);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => true,
        }
    );

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);

    useEffect(() => checkRedirection(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => handleMessage(), [lastMessage]);

    //Platform
    const checkRedirection = () => {
        Redirection.forGame()
    }

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
                        rightButtons={[]}
                    />
                </div>
            </div>

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