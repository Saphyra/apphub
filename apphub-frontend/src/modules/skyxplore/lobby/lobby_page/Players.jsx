import React, { useEffect, useState } from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import Stream from "../../../../common/js/collection/Stream";
import Endpoints from "../../../../common/js/dao/dao";
import Player from "./players/Player";
import PanelTitle from "./PanelTitle";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";

const Players = ({ localizationHandler, alliances, isHost, lastEvent, lobbyType }) => {
    const [players, setPlayers] = useState({});

    useEffect(() => loadPlayers(), []);
    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED) {
            console.log("Lobby Player modified", lastEvent.payload);
            const newPlayer = lastEvent.payload;

            const copy = new MapStream(players)
                .clone()
                .add(newPlayer.userId, newPlayer)
                .toObject();

            setPlayers(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_EXIT) {
            const copy = new MapStream(players)
                .filter(userId => userId !== lastEvent.payload.userId)
                .toObject();

            setPlayers(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED) {
            const newPlayer = lastEvent.payload.player;

            if (newPlayer === null) {
                return;
            }

            const copy = new MapStream(players)
                .clone()
                .add(newPlayer.userId, newPlayer)
                .toObject();

            setPlayers(copy);
        }
    }

    //Load
    const loadPlayers = () => {
        const fetch = async () => {
            const result = await Endpoints.SKYXPLORE_LOBBY_GET_PLAYERS.createRequest()
                .send();
            const playerMap = new Stream(result)
                .toMap((player) => player.userId);

            setPlayers(playerMap);
        }
        fetch();
    }

    const getPlayers = () => {
        return new MapStream(players)
            .map((userId, player) =>
                <Player
                    key={player.userId}
                    player={player}
                    localizationHandler={localizationHandler}
                    alliances={alliances}
                    isHost={isHost}
                    lobbyType={lobbyType}
                />
            )
            .toList();
    }

    return (
        <div id="skyxplore-lobby-players" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("players")} />

            <div className="skyxplore-lobby-panel-content">
                {getPlayers()}
            </div>
        </div>
    );
}

export default Players;