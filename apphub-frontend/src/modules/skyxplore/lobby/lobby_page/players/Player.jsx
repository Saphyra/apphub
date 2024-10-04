import React from "react";
import "./player/player.css"
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField"
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import { SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";

const Player = ({ player, localizationHandler, alliances, isHost, lobbyType }) => {
    const statusClass = "skyxplore-lobby-player-status-" + player.status.toLowerCase();

    const setAlliance = (event) => {
        const allianceValue = event.target.value;

        SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER.createRequest({ value: allianceValue }, { userId: player.userId })
            .send();
    }

    const getAllianceSelectMenu = () => {
        return (
            <select
                disabled={!isHost || player.status === "INVITED" || lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_LOAD}
                value={player.allianceId || ""}
                onChange={setAlliance}
            >
                <option value="no-alliance" >
                    {localizationHandler.get("no-alliance")}
                </option>
                {
                    new Stream(alliances)
                        .sorted((a, b) => a - b)
                        .map(alliance =>
                            <option
                                key={alliance.allianceId}
                                value={alliance.allianceId}
                            >
                                {alliance.allianceName}
                            </option>
                        )
                        .toList()
                }
                <option value="new-alliance">
                    {localizationHandler.get("new-alliance")}
                </option>
            </select>
        );
    }

    return (
        <div className={"skyxplore-lobby-player " + statusClass}>
            <h4 className="skyxplore-lobby-player-name">{player.characterName}</h4>
            <PreLabeledInputField
                className="skyxplore-lobby-player-alliance"
                label={localizationHandler.get("alliance") + ":"}
                input={getAllianceSelectMenu()}
            />
        </div>
    );
}

export default Player;