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
import Button from "../../../common/component/input/Button";
import Chat from "./lobby_page/Chat";
import Settings from "./lobby_page/Settings";
import Ais from "./lobby_page/Ais";
import Players from "./lobby_page/Players";
import Friends from "./lobby_page/Friends";
import Constants from "../../../common/js/Constants";
import Spinner from "../../../common/component/Spinner";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName";
import { SKYXPLORE_LOBBY_EXIT, SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS, SKYXPLORE_LOBBY_GET_ALLIANCES, SKYXPLORE_LOBBY_GET_SETTINGS, SKYXPLORE_LOBBY_START_GAME, SKYXPLORE_LOBBY_VIEW_FOR_PAGE } from "../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";
import useConnectToWebSocket from "../../../common/hook/ws/WebSocketFacade";
import { processAiModifiedEvent, processAiRemovedEvent, processAllianceCreatedEvent, processChatSendMessageEvent, processExitEvent, processPlayerConnectedEvent, processPlayerDisconnectedEvent, processPlayerModifiedEvent, processUserOfflineEvent, processUserOnlineEvent } from "./SkyXploreLobbyWebSocketProcessors";
import useLoader from "../../../common/hook/Loader";

const SkyXploreLobbyPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    //Platform
    const [lobbyData, setLobbyData] = useState({
        lobbyName: "",
        host: false,
        lobbyType: null,
        ownUserId: null,
        expectedPlayers: []
    });
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [displayStartGameDialog, setDisplayStartGameDialog] = useState(false);

    //Chat
    const [messages, setMessages] = useState([]);
    const [userReady, setUserReady] = useState(false);

    //Players
    const [alliances, setAlliances] = useState([]);
    const [ais, setAis] = useState([]);
    const [friends, setFriends] = useState([]);
    const [players, setPlayers] = useState({});

    //Settings
    const [settings, setSettings] = useState({
        maxPlayersPerSolarSystem: 0,
        additionalSolarSystems: {
            min: 0,
            max: 0
        },
        planetsPerSolarSystem: {
            min: 0,
            max: 0
        },
        planetSize: {
            min: 0,
            max: 0
        }
    });

    const { sendMessage } = useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_LOBBY,
        lastEvent => handleEvent(lastEvent)
    );

    useEffect(() => checkRedirection(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => loadLobbyData(), []);
    useEffect(() => loadAlliances(), []);

    useLoader(SKYXPLORE_LOBBY_GET_SETTINGS.createRequest(), setSettings);

    //Load
    const loadLobbyData = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_LOBBY_VIEW_FOR_PAGE.createRequest()
                .send();
            setLobbyData(response);
        }
        fetch();
    }

    const loadAlliances = () => {
        const fecth = async () => {
            const result = await SKYXPLORE_LOBBY_GET_ALLIANCES.createRequest()
                .send();
            setAlliances(result);
        }
        fecth();
    }

    //Platform
    const checkRedirection = () => {
        Redirection.forLobby()
    }

    //WebSocket event handlers
    const handleEvent = (event) => {
        switch (event.eventName) {
            case WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED:
                processAllianceCreatedEvent(event.payload, alliances, setAlliances, ais, setAis, players, setPlayers);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED:
                setDisplaySpinner(true);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED:
                window.location.href = Constants.SKYXPLORE_GAME_PAGE;
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE:
                processChatSendMessageEvent(event.payload, lobbyData.ownUserId, messages, setMessages);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_EXIT:
                processExitEvent(event.payload, localizationHandler, messages, setMessages, players, setPlayers);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED:
                processPlayerConnectedEvent(event.payload, localizationHandler, messages, setMessages);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED:
                processPlayerDisconnectedEvent(event.payload, localizationHandler, messages, setMessages);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED:
                processAiModifiedEvent(event.payload, ais, setAis);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED:
                processAiRemovedEvent(event.payload, ais, setAis);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE:
                processUserOnlineEvent(event.payload, friends, setFriends);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE:
                processUserOfflineEvent(event.payload.friendId, friends, setFriends);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED:
                processPlayerModifiedEvent(event.payload, players, setPlayers);
                break;
            case WebSocketEventName.SKYXPLORE_LOBBY_SETTINGS_MODIFIED:
                setSettings(event.payload);
                break;
        }
    }

    //Operations
    const startGame = async () => {
        if (lobbyData.lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_NEW) {
            sendStartGameRequest();
            return;
        }

        const response = await SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS.createRequest()
            .send();

        if (response.length > 0) {
            setDisplayStartGameDialog(true);
        } else {
            sendStartGameRequest();
        }
    }

    const sendStartGameRequest = () => {
        SKYXPLORE_LOBBY_START_GAME.createRequest()
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
        await SKYXPLORE_LOBBY_EXIT.createRequest()
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
                        sendWsMessage={sendMessage}
                        messages={messages}
                        setMessages={setMessages}
                    />

                    <div id="skyxplore-lobby-middle-bar">
                        {lobbyData.lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_NEW &&
                            <Settings
                                localizationHandler={localizationHandler}
                                isHost={lobbyData.host}
                                settings={settings}
                            />}
                        <Ais
                            localizationHandler={localizationHandler}
                            alliances={alliances}
                            isHost={lobbyData.host}
                            lobbyType={lobbyData.lobbyType}
                            ais={ais}
                            setAis={setAis}
                        />
                    </div>

                    <div id="skyxplore-lobby-right-bar">
                        <Friends
                            localizationHandler={localizationHandler}
                            friends={friends}
                            setFriends={setFriends}
                        />

                        <Players
                            localizationHandler={localizationHandler}
                            alliances={alliances}
                            isHost={lobbyData.host}
                            lobbyType={lobbyData.lobbyType}
                            players={players}
                            setPlayers={setPlayers}
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