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
import useConnectToWebSocket from "../../../common/hook/ws/WebSocketFacade";
import Button from "../../../common/component/input/Button";

const SkyXploreGamePage = () => {
    //===Platform
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [userId, setUserId] = useState("");
    const [isHost, setIsHost] = useState(false);

    useEffect(() => Redirection.forGame(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => fetchUserId(), []);
    useEffect(() => fetchIsHost(), []);

    const { lastEvent, sendMessage } = useConnectToWebSocket(WebSocketEndpoint.SKYXPLORE_GAME_MAIN, undefined,)

    const fetchUserId = () => {
        const fetch = async () => {
            const response = await Endpoints.GET_OWN_USER_ID.createRequest()
                .send();
            setUserId(response.value);
        }
        fetch();
    }

    const fetchIsHost = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GAME_IS_HOST.createRequest()
                .send();
            setIsHost(response.value);
        }
        fetch();
    }
    //===End Platform

    //===Chat
    const [hasUnreadMessage, setHasUnreadMessage] = useState(false);
    const [displayChat, setDisplayChat] = useState(false);
    //===End Chat

    const footer = () => {
        return <Footer
            leftButtons={[
                (isHost ?
                    <Button
                        key="save"
                        label={localizationHandler.get("save")}
                        onclick={save}
                    />
                    : []),
                <ExitGameButton
                    key="exit"
                    setConfirmationDialogData={setConfirmationDialogData}
                    isHost={isHost}
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

    const save = async () => {
        //TODO implement
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