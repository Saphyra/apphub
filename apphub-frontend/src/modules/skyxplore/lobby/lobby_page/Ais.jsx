import React, { useEffect, useState } from "react";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import Stream from "../../../../common/js/collection/Stream";
import Ai from "./ais/Ai";
import PanelTitle from "./PanelTitle";
import ValidatedInputField from "../../../../common/component/input/ValidatedInputField";
import ValidatedField from "../../../../common/js/validation/ValidatedField";
import validate from "../../../../common/js/validation/Validator";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";
import InputField from "../../../../common/component/input/InputField";
import { SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI, SKYXPLORE_LOBBY_GET_AIS } from "../../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";

const Ais = ({ localizationHandler, alliances, isHost, ais, setAis, lobbyType }) => {
    const [aiName, setAiName] = useState("");
    const [validationResult, setValidationResult] = useState({});

    useEffect(() => loadAis(), []);
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
            const result = await SKYXPLORE_LOBBY_GET_AIS.createRequest()
                .send();
            setAis(result);
        }
        fetch();
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
        await SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI.createRequest({ name: aiName })
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
                                    validationResultId="skyxplore-lobby-create-ai-name-validation"
                                    validationResult={validationResult[ValidatedField.CHARACTER_NAME]}
                                    inputField={<InputField
                                        id="skyxplore-lobby-create-ai-name"
                                        type="text"
                                        placeholder={localizationHandler.get("ai-name")}
                                        onchangeCallback={setAiName}
                                        value={aiName}
                                    />}
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