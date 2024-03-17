import React, { useEffect, useState } from "react";
import localizationData from "./email_changer_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import ValidatedInputField from "../../../common/component/input/ValidatedInputField";
import { validateEmail, validateFilled } from "../validation/AccountInputValidator";
import ValidationResult from "../../../common/js/validation/ValidationResult";
import Button from "../../../common/component/input/Button";
import Endpoints from "../../../common/js/dao/dao";
import NotificationService from "../../../common/js/notification/NotificationService";
import InputField from "../../../common/component/input/InputField";

const EmailChanger = ({ userData, setUserData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newEmail, setNewEmail] = useState("");
    const [password, setPassword] = useState("");

    const [emailValidationResult, setEmailValidationResult] = useState(new ValidationResult(false));
    const [passwordValidationResult, setPassswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setEmailValidationResult(validateEmail(newEmail)), [newEmail]);
    useEffect(() => setPassswordValidationResult(validateFilled(password)), [password]);

    const changeEmail = async () => {
        const response = await Endpoints.ACCOUNT_CHANGE_EMAIL.createRequest({ email: newEmail, password: password })
            .send();

        setNewEmail("");
        setPassword("");
        setUserData(response);
        NotificationService.showSuccess(localizationHandler.get("email-changed"));
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
                    <span id="account-current-email">{userData.email}</span>
                    <span>{")"}</span>
                </div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("new-email")}
                            input={<ValidatedInputField
                                validationResultId="account-change-email-email-input-validation"
                                validationResult={emailValidationResult}
                                inputField={<InputField
                                    id="account-change-email-email-input"
                                    type="email"
                                    placeholder={localizationHandler.get("new-email")}
                                    value={newEmail}
                                    onchangeCallback={setNewEmail}
                                />}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-email-password-input-validation"
                                validationResult={passwordValidationResult}
                                inputField={<InputField
                                    id="account-change-email-password-input"
                                    placeholder={localizationHandler.get("password")}
                                    value={password}
                                    onchangeCallback={setPassword}
                                    type="password"
                                />}
                            />}
                        />
                    </div>

                    <Button
                        id="account-change-email-button"
                        className="account-save-button"
                        label={localizationHandler.get("save")}
                        disabled={!emailValidationResult.valid || !passwordValidationResult.valid}
                        onclick={changeEmail}
                    />
                </div>
            </div>
        </div>
    );
}

export default EmailChanger;