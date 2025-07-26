import React, { useEffect, useState } from "react";
import localizationData from "./skyxplore_game_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Redirection from "../Redirection";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Footer from "../../../common/component/Footer";
import PauseAndResumeGameButton from "./pause_and_resume/button/PauseAndResumeGameButton";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import ExitGameButton from "./exit_game/ExitGameButton";
import ToggleChatButton from "./chat/toggle_button/ToggleChatButton";
import Chat from "./chat/Chat";
import { ToastContainer } from "react-toastify";
import Navigation from "./navigation/Navigation";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint";
import useConnectToWebSocket from "../../../common/hook/ws/WebSocketFacade";
import Button from "../../../common/component/input/Button";
import Spinner from "../../../common/component/Spinner";
import { GET_OWN_USER_ID } from "../../../common/js/dao/endpoints/GenericEndpoints";
import { SKYXPLORE_GAME_IS_HOST, SKYXPLORE_GAME_PAUSE, SKYXPLORE_GAME_SAVE, SKYXPLORE_PROCESS_TICK } from "../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName";
import { addAndSet, hasValue, isTrue } from "../../../common/js/Utils";
import MapStream from "../../../common/js/collection/MapStream";
import ChatConstants from "./chat/ChatConstants";
import ConfirmationDialogData from "../../../common/component/confirmation_dialog/ConfirmationDialogData";
import useLoader from "../../../common/hook/Loader";
import { IS_ADMIN } from "../../../common/js/dao/endpoints/UserEndpoints";

const SkyXploreGamePage = () => {
    //===Platform
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [userId, setUserId] = useState("");
    const [isAdmin, setIsAdmin] = useState(false);
    const [isHost, setIsHost] = useState(false);

    const [paused, setPaused] = useState(true);

    //Chat
    const [messages, setMessages] = useState({});
    const [currentChatRoom, setCurrentChatRoom] = useState(ChatConstants.GENERAL_CHAT_ROOM);
    const [unreadMessages, setUnreadMessages] = useState({});
    const [chatRooms, setChatRooms] = useState([]);

    useEffect(() => Redirection.forGame(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => fetchUserId(), []);
    useEffect(() => fetchIsHost(), []);

    useLoader({ request: IS_ADMIN.createRequest(), mapper: (r) => setIsAdmin(r.value) });

    const { sendMessage } = useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_GAME_MAIN,
        event => processEvent(event)
    );

    const fetchUserId = () => {
        const fetch = async () => {
            const response = await GET_OWN_USER_ID.createRequest()
                .send();
            setUserId(response.value);
        }
        fetch();
    }

    const fetchIsHost = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_GAME_IS_HOST.createRequest()
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

    const processEvent = (event) => {
        switch (event.eventName) {
            case WebSocketEventName.SKYXPLORE_GAME_PAUSED:
                setPaused(isTrue(event.payload));
                break;
            case WebSocketEventName.SKYXPLORE_GAME_USER_JOINED:
            case WebSocketEventName.SKYXPLORE_GAME_USER_LEFT:
            case WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE:
                processChatEvent(event)
                break;
            case WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED:
                addAndSet(chatRooms, event.payload, setChatRooms);
                break;
            case WebSocketEventName.SKYXPLORE_GAME_PLAYER_DISCONNECTED:
                isHost && processPlayerDisconnectedEvent(event);
                break;
            case WebSocketEventName.SKYXPLORE_GAME_PLAYER_RECONNECTED:
                isHost && processPlayerConnectedEvent(event);
                break;
        }
    }

    const processChatEvent = (event) => {
        const roomId = event.payload.room;

        const room = messages[roomId] || [];

        room.push(event);

        messages[roomId] = room;

        const newMessages = new MapStream(messages)
            .add(roomId, room)
            .toObject();

        setMessages(newMessages);

        if (hasValue(event) && hasValue(event.payload) && hasValue(event.payload.room)) {
            if (!displayChat || event.payload.room !== currentChatRoom) {
                setMessageStatus(event.payload.room, true);
            }
        }
    }

    const setMessageStatus = (roomId, status) => {
        const copy = new MapStream(unreadMessages)
            .add(roomId, status)
            .toObject();
        setUnreadMessages(copy);
    }

    const processPlayerDisconnectedEvent = (lastEvent) => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "skyxplore-game-player-disconnected-dialog",
            localizationHandler.get("player-disconnected-title"),
            localizationHandler.get("player-disconnected-detail", { playerName: lastEvent.payload.value }),
            [
                <Button
                    key="confirm"
                    id="skyxplore-game-player-disconnected-confirm-button"
                    label={localizationHandler.get("confirm")}
                    onclick={closeConfirmationDialog}
                />,
                <Button
                    key="resume"
                    id="skyxplore-game-player-disconnected-resume-button"
                    label={localizationHandler.get("resume")}
                    onclick={resumeGame}
                />
            ]
        ));
    }

    const processPlayerConnectedEvent = (lastEvent) => {
        if (!hasValue(confirmationDialogData)) {
            return;
        }

        setConfirmationDialogData(new ConfirmationDialogData(
            "skyxplore-game-player-reconnected-dialog",
            localizationHandler.get("player-reconnected-title"),
            localizationHandler.get("player-reconnected-detail", { playerName: lastEvent.payload.value }),
            [
                <Button
                    key="close"
                    id="skyxplore-game-player-reconnected-close-button"
                    label={localizationHandler.get("close")}
                    onclick={closeConfirmationDialog}
                />,
                <Button
                    key="resume"
                    id="skyxplore-game-player-reconnected-resume-button"
                    label={localizationHandler.get("resume")}
                    onclick={resumeGame}
                />
            ]
        ));
    }

    const closeConfirmationDialog = () => {
        setConfirmationDialogData(null);
    }

    const resumeGame = () => {
        setConfirmationDialogData(null);

        SKYXPLORE_GAME_PAUSE.createRequest({ value: false })
            .send();
    }

    const processTick = async () => {
        await SKYXPLORE_PROCESS_TICK.createRequest()
            .send(setDisplaySpinner);
    }

    const footer = () => {
        return <Footer
            leftButtons={[
                (isHost ?
                    <Button
                        key="save"
                        label={localizationHandler.get("save")}
                        onclick={save}
                    />
                    : []
                ),
                <ExitGameButton
                    key="exit"
                    setConfirmationDialogData={setConfirmationDialogData}
                    isHost={isHost}
                    setDisplaySpinner={setDisplayChat}
                />,
                <PauseAndResumeGameButton
                    key="pause-and-resume"
                    isHost={isHost}
                    paused={paused}
                />
            ]}
            centerButtons={[
                (isAdmin ?
                    <Button
                        key="tick"
                        id="skyxplore-game-process-tick-button"
                        label={localizationHandler.get("process-tick")}
                        onclick={processTick}
                    />
                    : []
                )
            ]}
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
        setDisplaySpinner(true);

        await SKYXPLORE_GAME_SAVE.createRequest()
            .send();

        setDisplaySpinner(false);
        NotificationService.showSuccess(localizationHandler.get("game-saved"));
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
                userId={userId}
                sendMessage={sendMessage}
                messages={messages}
                setMessageStatus={setMessageStatus}
                unreadMessages={unreadMessages}
                currentChatRoom={currentChatRoom}
                setCurrentChatRoom={setCurrentChatRoom}
                chatRooms={chatRooms}
                setChatRooms={setChatRooms}
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner && <Spinner />}

            <ToastContainer />
        </div>
    )
}

export default SkyXploreGamePage