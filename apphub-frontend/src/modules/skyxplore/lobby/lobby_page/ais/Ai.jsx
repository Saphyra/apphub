import React, { useEffect, useState } from "react";
import Button from "../../../../../common/component/input/Button";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import Stream from "../../../../../common/js/collection/Stream";
import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
import "./ai/ai.css"
import InputField from "../../../../../common/component/input/InputField";

const Ai = ({ ai, localizationHandler, alliances, isHost, lobbyType }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [aiName, setAiName] = useState(ai.name);

    useEffect(() => setAiName(ai.name), [ai.name]);

    const setAlliance = (event) => {
        const allianceValue = event.target.value;

        Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI.createRequest({ value: allianceValue }, { userId: ai.userId })
            .send();
    }

    const removeAi = () => {
        Endpoints.SKYXPLORE_LOBBY_REMOVE_AI.createRequest(null, { userId: ai.userId })
            .send();
    }

    const changeAiName = () => {
        if (aiName.length >= 3) {
            ai.name = aiName;
            Endpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI.createRequest(ai)
                .send();
        } else {
            setAiName(ai.name);
        }

        setEditingEnabled(false);
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

    const getAiName = () => {
        if (editingEnabled) {
            return (
                <InputField
                    className="skyxplore-lobby-member-name skyxplore-lobby-change-ai-name"
                    type="text"
                    placeholder={localizationHandler.get("ai-name")}
                    value={aiName}
                    onchangeCallback={setAiName}
                    onfocusOutCallback={changeAiName}
                    autoFocus={true}
                />
            )
        } else {
            return (
                <h4
                    className="skyxplore-lobby-member-name skyxplore-lobby-ai-name"
                    onClick={() => isHost && setEditingEnabled(true)}
                >
                    {aiName}
                </h4>
            );
        }
    }

    return (
        <div className="skyxplore-lobby-ai">
            {getAiName()}

            <Button
                className="skyxplore-lobby-remove-ai-button"
                label="X"
                onclick={removeAi}
                disabled={!isHost}
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