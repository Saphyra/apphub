import React from "react";
import Button from "../../../../../common/component/input/Button";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
import "./ai/ai.css"

const Ai = ({ ai, localizationHandler, alliances, isHost, lobbyType }) => {
    const setAlliance = (event) => {
        const allianceValue = event.target.value;

        Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI.createRequest({ value: allianceValue }, { userId: ai.userId })
            .send();
    }

    const removeAi = () => {
        Endpoints.SKYXPLORE_LOBBY_REMOVE_AI.createRequest(null, { userId: ai.userId })
            .send();
    }

    const getAllianceSelectMenu = () => {
        return (
            <select
                disabled={!isHost || lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_LOAD}
                value={ai.allianceId || ""}
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
        <div className="skyxplore-lobby-ai">
            <h4 className="skyxplore-lobby-member-name">{ai.name}</h4>
            <Button
                className="skyxplore-lobby-remove-ai-button"
                label="X"
                onclick={removeAi}
            />

            <PreLabeledInputField
                className="skyxplore-lobby-member-alliance"
                label={localizationHandler.get("alliance") + ":"}
                input={getAllianceSelectMenu()}
            />
        </div>
    );
}

export default Ai;