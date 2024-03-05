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
import InputField from "../../../common/component/input/InputField";

const UsernameChanger = ({ userData, setUserData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newUsername, setNewUsername] = useState("");
    const [password, setPassword] = useState("");

    const [usernaemValidationResult, setUsernameValidationResult] = useState(new ValidationResult(false));
    const [passwordValidationResult, setPassswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setUsernameValidationResult(validateUsername(newUsername)), [newUsername]);
    useEffect(() => setPassswordValidationResult(validateFilled(password)), [password]);

    const changeUsername = async () => {
        const response = await Endpoints.ACCOUNT_CHANGE_USERNAME.createRequest({ username: newUsername, password: password })
            .send();

        setNewUsername("");
        setPassword("");
        setUserData(response);
        NotificationService.showSuccess(localizationHandler.get("username-changed"));
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">
                    <span>{localizationHandler.get("tab-title")}</span>
                    <span> </span>
                    <span>{"("}</span>
                    <span>{localizationHandler.get("current")}</span>
                    <span>: </span>
                    <span id="account-current-username">{userData.username}</span>
                    <span>{")"}</span>
                </div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("new-username")}
                            input={<ValidatedInputField
                                validationResultId="account-change-username-username-input-validation"
                                validationResult={usernaemValidationResult}
                                inputField={<InputField
                                    id="account-change-username-username-input"
                                    placeholder={localizationHandler.get("new-username")}
                                    value={newUsername}
                                    onchangeCallback={setNewUsername}
                                />}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-username-password-input-validation"
                                validationResult={passwordValidationResult}
                                inputField={<InputField
                                    id="account-change-username-password-input"
                                    placeholder={localizationHandler.get("password")}
                                    value={password}
                                    onchangeCallback={setPassword}
                                    type="password"
                                />}
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