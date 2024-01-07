import React, { useEffect, useState } from "react";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import Stream from "../../../../common/js/collection/Stream";
import Endpoints from "../../../../common/js/dao/dao";
import Ai from "./ais/Ai";
import PanelTitle from "./PanelTitle";
import ValidatedInputField from "../../../../common/component/input/ValidatedInputField";
import ValidatedField from "../../../../common/js/validation/ValidatedField";
import validate from "../../../../common/js/validation/Validator";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";

const Ais = ({ localizationHandler, alliances, isHost, lastEvent, lobbyType }) => {
    const [ais, setAis] = useState([]);
    const [aiName, setAiName] = useState("");
    const [validationResult, setValidationResult] = useState({});

    useEffect(() => loadAis(), []);
    useEffect(() => handleEvent(), [lastEvent]);
    useEffect(() => runValidation(), [aiName]);

    const runValidation = () => {
        const fields = {};
        fields[ValidatedField.CHARACTER_NAME] = aiName;
        setValidationResult(validate(fields, localizationHandler));
    }

    const isFormValid = () => {
        return Object.values(validationResult)
            .every((validation) => validation.valid);
    }

    const loadAis = () => {
        const fetch = async () => {
            const result = await Endpoints.SKYXPLORE_LOBBY_GET_AIS.createRequest()
                .send();
            setAis(result);
        }
        fetch();
    }

    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED) {
            const newAi = lastEvent.payload;

            const copy = new Stream(ais)
                .filter(ai => ai.userId !== newAi.userId)
                .add(newAi)
                .toList();

            setAis(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED) {
            const copy = new Stream(ais)
                .filter(ai => ai.userId !== lastEvent.payload)
                .toList();

            setAis(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED) {
            const newAi = lastEvent.payload.ai;

            if (newAi === null) {
                return;
            }

            const copy = new Stream(ais)
                .filter(ai => ai.userId !== newAi.userId)
                .add(newAi)
                .toList();

            setAis(copy);
        }
    }

    const getAis = () => {
        return new Stream(ais)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(ai =>
                <Ai
                    key={ai.userId}
                    ai={ai}
                    localizationHandler={localizationHandler}
                    alliances={alliances}
                    isHost={isHost}
                    lobbyType={lobbyType}
                />
            )
            .toList();
    }

    const createAi = async () => {
        await Endpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI.createRequest({ name: aiName })
            .send();

        setAiName("");
    }

    return (
        <div id="skyxplore-lobby-ais" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("ais")} />
            <div className="skyxplore-lobby-panel-content">
                {getAis()}
                {ais.length < 10 && lobbyType === Constants.SKYXPLORE_LOBBY_TYPE_NEW && isHost &&
                    <div id="skyxplore-lobby-create-ai" >
                        <h4 className="skyxplore-lobby-player-name">{localizationHandler.get("create-ai")}</h4>

                        <PreLabeledInputField
                            label={localizationHandler.get("ai-name") + ":"}
                            input={
                                <ValidatedInputField
                                    id="skyxplore-lobby-create-ai-name-validation"
                                    inputId="skyxplore-lobby-create-ai-name"
                                    validationResult={validationResult[ValidatedField.CHARACTER_NAME]}
                                    type="text"
                                    placeholder={localizationHandler.get("ai-name")}
                                    onchangeCallback={setAiName}
                                    value={aiName}
                                />
                            }
                        />

                        <Button
                            id="skyxplore-lobby-create-ai-button"
                            label={localizationHandler.get("create")}
                            disabled={!isFormValid()}
                            onclick={createAi}
                        />
                    </div>
                }
            </div>
        </div>
    );
}

export default Ais;