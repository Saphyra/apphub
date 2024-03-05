import React, { useEffect, useState } from "react";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import ValidatedInputField from "../../../../common/component/input/ValidatedInputField";
import ValidatedField from "../../../../common/js/validation/ValidatedField"
import validate from "../../../../common/js/validation/Validator";
import Endpoints from "../../../../common/js/dao/dao";
import Constants from "../../../../common/js/Constants";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import InputField from "../../../../common/component/input/InputField";


const NewGameConfirmationDialog = ({ localizationHandler, setDisplaynNewGameConfirmationDialog }) => {
    const [gameName, setGameName] = useState("");
    const [validationResult, setValidationResult] = useState({});

    useEffect(() => runValidation(), [gameName]);

    const runValidation = () => {
        const fields = {};
        fields[ValidatedField.GAME_NAME] = gameName;
        setValidationResult(validate(fields, localizationHandler));
    }

    const isFormValid = () => {
        return Object.values(validationResult)
            .every((validation) => validation.valid);
    }

    const createLobby = async () => {
        await Endpoints.SKYXPLORE_CREATE_LOBBY.createRequest({ value: gameName })
            .send();

        window.location.href = Constants.SKYXPLORE_LOBBY_PAGE;
    }

    return (
        <div className="centered">
            <ConfirmationDialog
                id="skyxplore-new-game"
                title={localizationHandler.get("new-game")}
                content={
                    <PreLabeledInputField
                        label={localizationHandler.get("game-name") + ":"}
                        input={
                            <ValidatedInputField
                                validationResultId="skyxplore-game-name-validation"
                                validationResult={validationResult[ValidatedField.GAME_NAME]}
                                inputField={<InputField
                                    id="skyxplore-game-name"
                                    type="text"
                                    placeholder={localizationHandler.get("game-name")}
                                    onchangeCallback={setGameName}
                                    value={gameName}
                                />}
                            />
                        }
                    />
                }
                choices={[
                    <Button
                        key="create"
                        id="skyxplore-create-new-game-button"
                        onclick={createLobby}
                        label={localizationHandler.get("create")}
                        disabled={!isFormValid()}
                    />,
                    <Button
                        key="cancel"
                        id="skyxplore-cancel-game-creation-button"
                        onclick={() => setDisplaynNewGameConfirmationDialog(false)}
                        label={localizationHandler.get("cancel")}
                    />
                ]}
            />
        </div>
    );
}

export default NewGameConfirmationDialog;