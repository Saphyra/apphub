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
import { useWebSocket } from "react-use-websocket/dist/lib/use-websocket";
import Invitations from "./main_menu_page/Invitations";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName";

const SkyXploreMainMenuPage = () => {
    const mainMenuWsUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_MAIN_MENU;
    const invitationWsUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_INVITATION;
    const localizationHandler = new LocalizationHandler(localizationData);
    const [lastEvent, setLastEvent] = useState(null);
    const [displaynNewGameConfirmationDialog, setDisplaynNewGameConfirmationDialog] = useState(false);
    const { sendMessage: sendMainMenuMessage, lastMessage: mainMenuLastMessage } = useWebSocket(
        mainMenuWsUrl,
        {
            share: true,
            shouldReconnect: () => false,
        }
    );
    const { sendMessage: sendInvitationMessage, lastMessage: invitationLastMessage } = useWebSocket(
        invitationWsUrl,
        {
            share: true,
            shouldReconnect: () => false,
        }
    );

    useEffect(() => initPage(), []);

    useEffect(() => handleMessage(mainMenuLastMessage, sendMainMenuMessage), [mainMenuLastMessage]);
    useEffect(() => handleMessage(invitationLastMessage, sendInvitationMessage), [invitationLastMessage]);

    const initPage = () => {
        Redirection.forMainMenu();
        sessionChecker();
        NotificationService.displayStoredMessages()
    }

    const handleMessage = (lastMessage, sendMessage) => {
        if (lastMessage === null) {
            return;
        }

        const message = JSON.parse(lastMessage.data);

        if (message.eventName === WebSocketEventName.PING) {
            sendMessage(lastMessage.data);
        }

        setLastEvent(message);
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