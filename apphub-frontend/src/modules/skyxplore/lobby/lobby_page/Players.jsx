import React, { useEffect } from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import Stream from "../../../../common/js/collection/Stream";
import Player from "./players/Player";
import PanelTitle from "./PanelTitle";
import { SKYXPLORE_LOBBY_GET_PLAYERS } from "../../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";

const Players = ({ localizationHandler, alliances, isHost, lobbyType, players, setPlayers }) => {
    useEffect(() => loadPlayers(), []);

    //Load
    const loadPlayers = () => {
        const fetch = async () => {
            const result = await SKYXPLORE_LOBBY_GET_PLAYERS.createRequest()
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