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

const EmailChanger = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newEmail, setNewEmail] = useState("");
    const [password, setPassword] = useState("");

    const [emailValidationResult, setEmailValidationResult] = useState(new ValidationResult(false));
    const [passwordValidationResult, setPassswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setEmailValidationResult(validateEmail(newEmail)), [newEmail]);
    useEffect(() => setPassswordValidationResult(validateFilled(password)), [password]);

    const changeEmail = async () => {
        await Endpoints.ACCOUNT_CHANGE_EMAIL.createRequest({ email: newEmail, password: password })
            .send();

        setNewEmail("");
        setPassword("");
        NotificationService.showSuccess(localizationHandler.get("email-changed"));
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">{localizationHandler.get("tab-title")}</div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("new-email")}
                            input={<ValidatedInputField
                                validationResultId="account-change-email-email-input-validation"
                                inputId="account-change-email-email-input"
                                placeholder={localizationHandler.get("new-email")}
                                value={newEmail}
                                onchangeCallback={setNewEmail}
                                validationResult={emailValidationResult}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-email-password-input-validation"
                                inputId="account-change-email-password-input"
                                placeholder={localizationHandler.get("password")}
                                value={password}
                                onchangeCallback={setPassword}
                                validationResult={passwordValidationResult}
                                type="password"
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