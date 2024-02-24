import React, { useEffect, useState } from "react";
import localizationData from "./username_changer_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import ValidationResult from "../../../common/js/validation/ValidationResult";
import { validateFilled, validateUsername } from "../validation/AccountInputValidator";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import ValidatedInputField from "../../../common/component/input/ValidatedInputField";
import Button from "../../../common/component/input/Button";
import Endpoints from "../../../common/js/dao/dao";
import NotificationService from "../../../common/js/notification/NotificationService";

const UsernameChanger = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newUsername, setNewUsername] = useState("");
    const [password, setPassword] = useState("");

    const [usernaemValidationResult, setUsernameValidationResult] = useState(new ValidationResult(false));
    const [passwordValidationResult, setPassswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setUsernameValidationResult(validateUsername(newUsername)), [newUsername]);
    useEffect(() => setPassswordValidationResult(validateFilled(password)), [password]);

    const changeUsername =async () => {
        await Endpoints.ACCOUNT_CHANGE_USERNAME.createRequest({ username: newUsername, password: password })
            .send();

        setNewUsername("");
        setPassword("");
        NotificationService.showSuccess(localizationHandler.get("username-changed"));
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">{localizationHandler.get("tab-title")}</div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("new-username")}
                            input={<ValidatedInputField
                                validationResultId="account-change-username-username-input-validation"
                                inputId="account-change-username-username-input"
                                placeholder={localizationHandler.get("new-username")}
                                value={newUsername}
                                onchangeCallback={setNewUsername}
                                validationResult={usernaemValidationResult}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-username-password-input-validation"
                                inputId="account-change-username-password-input"
                                placeholder={localizationHandler.get("password")}
                                value={password}
                                onchangeCallback={setPassword}
                                validationResult={passwordValidationResult}
                                type="password"
                            />}
                        />

                    </div>

                    <Button
                        id="account-change-username-button"
                        className="account-save-button"
                        label={localizationHandler.get("save")}
                        disabled={!usernaemValidationResult.valid || !passwordValidationResult.valid}
                        onclick={changeUsername}
                    />
                </div>
            </div>
        </div>
    )
}

export default UsernameChanger;