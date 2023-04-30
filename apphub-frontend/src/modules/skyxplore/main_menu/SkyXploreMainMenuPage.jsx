import React, { useEffect, useState } from "react";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./main_menu_resources/page_localization.json";
import Redirection from "../Redirection";
import "../skyxplore.css";
import Header from "../../../common/component/Header";
import "./main_menu_resources/main_menu_page.css";
import { ToastContainer } from "react-toastify";
import Contacts from "./main_menu_page/Contacts";
import NewGameConfirmationDialog from "./main_menu_page/NewGameConfirmationDialog";
import MainMenuButtons from "./main_menu_page/MainMenuButtons";
import WebSocketEndpoint from "../../../common/js/ws/WebSocketEndpoint";
import { useWebSocket } from "react-use-websocket/dist/lib/use-websocket";
import WebSocketEventName from "../../../common/js/ws/WebSocketEventName";
import Invitations from "./main_menu_page/Invitations";

const SkyXploreMainMenuPage = () => {
    const webSocketUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_MAIN_MENU;
    const localizationHandler = new LocalizationHandler(localizationData);
    const [lastEvent, setLastEvent] = useState(null);
    const [displaynNewGameConfirmationDialog, setDisplaynNewGameConfirmationDialog] = useState(false);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => false,
        }
    );

    useEffect(() => checkRedirection(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
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

    const checkRedirection = () => {
        Redirection.forMainMenu()
    }

    document.title = localizationHandler.get("title");

    return (
        <div>
            <div className="skyxplore-background" />

            <div id="skyxplore-main-menu" className="main-page skyxplore-main">
                <Header label={localizationHandler.get("page-title")} />

                <main className="footerless">
                    <MainMenuButtons
                        localizationHandler={localizationHandler}
                        setDisplaynNewGameConfirmationDialog={setDisplaynNewGameConfirmationDialog}
                    />

                    <Contacts
                        localizationHandler={localizationHandler}
                        lastEvent={lastEvent}
                    />

                    <Invitations
                        localizationHandler={localizationHandler}
                        lastEvent={lastEvent}
                    />
                </main>
            </div>

            {displaynNewGameConfirmationDialog && <NewGameConfirmationDialog
                localizationHandler={localizationHandler}
                setDisplaynNewGameConfirmationDialog={setDisplaynNewGameConfirmationDialog}
            />}
            <ToastContainer />
        </div>
    );
}

export default SkyXploreMainMenuPage;