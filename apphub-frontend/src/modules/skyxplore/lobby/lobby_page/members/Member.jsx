import React from "react";
import "./lobbyMember/lobbyMember.css"
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField"
import Stream from "../../../../../common/js/collection/Stream";
import Endpoints from "../../../../../common/js/dao/dao";
import Constants from "../../../../../common/js/Constants";

const Member = ({ lobbyMember, localizationHandler, alliances, isHost, lobbyType }) => {
    const statusClass = "skyxplore-lobby-lobbyMember-status-" + lobbyMember.status.toLowerCase();

    const setAlliance = (event) => {
        const allianceValue = event.target.value;

        Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER.createRequest({ value: allianceValue }, { userId: lobbyMember.userId })
            .send();
    }

    const getAllianceSelectMenu = () => {
        return (
            <select
                disabled={!isHost || lobbyMember.status === "INVITED" || lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_LOAD}
                value={lobbyMember.allianceId || ""}
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
        <div className={"skyxplore-lobby-lobbyMember " + statusClass}>
            <h4 className="skyxplore-lobby-lobbyMember-name">{lobbyMember.characterName}</h4>
            <PreLabeledInputField
                className="skyxplore-lobby-lobbyMember-alliance"
                label={localizationHandler.get("alliance") + ":"}
                input={getAllianceSelectMenu()}
            />
        </div>
    );
}

export default Member;