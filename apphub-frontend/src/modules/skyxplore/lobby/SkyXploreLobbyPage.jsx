import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./lobby_resources/page_localization.json";
import Redirection from "../Redirection";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import "../skyxplore.css";
import "./lobby_resources/lobby.css";
import { ToastContainer } from "react-toastify";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Endpoints from "../../../common/js/dao/dao";
import Button from "../../../common/component/input/Button";
import WebSocketEndpoint from "../../../common/js/ws/WebSocketEndpoint";
import { useWebSocket } from "react-use-websocket/dist/lib/use-websocket";
import WebSocketEventName from "../../../common/js/ws/WebSocketEventName";
import Chat from "./lobby_page/Chat";
import Settings from "./lobby_page/Settings";
import Ais from "./lobby_page/Ais";
import Members from "./lobby_page/Members";
import Friends from "./lobby_page/Friends";
import Constants from "../../../common/js/Constants";
import Spinner from "../../../common/component/Spinner";
import Stream from "../../../common/js/collection/Stream";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";

const SkyXploreLobbyPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");
    const webSocketUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_LOBBY;

    const [lobbyData, setLobbyData] = useState({
        lobbyName: "",
        host: false,
        lobbyType: null,
        ownUserId: null,
        expectedPlayers: []
    });
    const [userReady, setUserReady] = useState(false);
    const [lastEvent, setLastEvent] = useState(null);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => false,
        }
    );
    const [alliances, setAlliances] = useState([]);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [displayStartGameDialog, setDisplayStartGameDialog] = useState(false);

    useEffect(() => checkRedirection(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => handleMessage(), [lastMessage]);
    useEffect(() => loadLobbyData(), []);
    useEffect(() => loadAlliances(), []);
    useEffect(() => handleEvent(), [lastEvent]);

    //Load
    const loadLobbyData = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_LOBBY_VIEW_FOR_PAGE.createRequest()
                .send();
            setLobbyData(response);
        }
        fetch();
    }

    const loadAlliances = () => {
        const fecth = async () => {
            const result = await Endpoints.SKYXPLORE_LOBBY_GET_ALLIANCES.createRequest()
                .send();
            setAlliances(result);
        }
        fecth();
    }

    //Platform
    const checkRedirection = () => {
        Redirection.forLobby()
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

    //WebSocket event handlers
    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED) {
            const copy = new Stream(alliances)
                .add(lastEvent.payload.alliance)
                .toList();
            setAlliances(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED) {
            setDisplaySpinner(true);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED) {
            window.location.href = Constants.SKYXPLORE_GAME_PAGE;
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_EXIT) {
            if (lastEvent.payload.host) {
                window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
            }
        }
    }

    //Operations
    const startGame = async () => {
        if (lobbyData.lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_NEW) {
            console.log("New game can be started without confirmation");
            sendStartGameRequest();
            return;
        }

        const response = await Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS.createRequest()
            .send();

        console.log(response.length);

        if (response.length > 0) {
            console.log("Asking the user if he wants to start the game without every player");
            setDisplayStartGameDialog(true);
        } else {
            console.log("Every player is joined, starting the game without confirmation");
            sendStartGameRequest();
        }
    }

    const sendStartGameRequest = () => {
        Endpoints.SKYXPLORE_LOBBY_START_GAME.createRequest()
            .send();
    }

    const setReadyStatus = (status) => {
        setUserReady(status);

        const event = {
            eventName: WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS,
            payload: status
        }

        sendMessage(JSON.stringify(event));
    }

    const exit = async () => {
        await Endpoints.SKYXPLORE_LOBBY_EXIT.createRequest()
            .send();

        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
    }

    //Buttons
    const startGameButton = <Button
        key="start-game"
        id="skyxplore-lobby-start-game-button"
        label={localizationHandler.get("start-game")}
        onclick={() => startGame()}
    />
    const setReadyButton = <Button
        key="set-ready"
        id="skyxplore-lobby-set-ready-button"
        label={localizationHandler.get("ready")}
        onclick={() => setReadyStatus(true)}

    />
    const setUnreadyButton = <Button
        key="set-unready"
        id="skyxplore-lobby-set-unready-button"
        label={localizationHandler.get("unready")}
        onclick={() => setReadyStatus(false)}
    />
    const leaveLobbyButton = <Button
        key="exit"
        id="skyxplore-lobby-leave-button"
        label={localizationHandler.get("exit")}
        onclick={() => exit()}
    />

    return (
        <div>
            <div className="skyxplore-background" />

            <div id="skyxplore-lobby" className="main-page skyxplore-main" >
                <Header label={localizationHandler.get("page-title", { lobbyName: lobbyData.lobbyName })} />

                <main>
                    <Chat
                        localizationHandler={localizationHandler}
                        lastEvent={lastEvent}
                        sendWsMessage={sendMessage}
                        ownUserId={lobbyData.ownUserId}
                    />

                    <div id="skyxplore-lobby-middle-bar">
                        {lobbyData.lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_NEW &&
                            <Settings
                                localizationHandler={localizationHandler}
                                isHost={lobbyData.host}
                                lastEvent={lastEvent}
                            />}
                        <Ais
                            localizationHandler={localizationHandler}
                            alliances={alliances}
                            isHost={lobbyData.host}
                            lastEvent={lastEvent}
                            lobbyType={lobbyData.lobbyType}
                        />
                    </div>

                    <div id="skyxplore-lobby-right-bar">
                        <Friends
                            localizationHandler={localizationHandler}
                            lastEvent={lastEvent}
                        />

                        <Members
                            localizationHandler={localizationHandler}
                            alliances={alliances}
                            isHost={lobbyData.host}
                            lastEvent={lastEvent}
                            lobbyType={lobbyData.lobbyType}
                        />
                    </div>
                </main>

                <Footer
                    leftButtons={userReady ? [setUnreadyButton] : [setReadyButton]}
                    centerButtons={lobbyData.host ? [startGameButton] : []}
                    rightButtons={[leaveLobbyButton]}
                />
            </div>

            {displaySpinner && <Spinner />}

            {displayStartGameDialog &&
                <ConfirmationDialog
                    id="skyxplore-confirm-load-game"
                    title={localizationHandler.get("not-everyone-joined-title")}
                    content={localizationHandler.get("not-everyone-joined-content")}
                    choices={[
                        <Button
                            key="start"
                            id="skyxplore-start-game-anyways"
                            onclick={sendStartGameRequest}
                            label={localizationHandler.get("start-game-anyways")}
                        />,
                        <Button
                            key="cancel"
                            id="skyxplore-do-not-start"
                            onclick={() => setDisplayStartGameDialog(false)}
                            label={localizationHandler.get("cancel")}
                        />
                    ]}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default SkyXploreLobbyPage;