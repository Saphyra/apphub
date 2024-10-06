import React, { useEffect, useState } from "react";
import localizationData from "./password_changer_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import ValidationResult from "../../../common/js/validation/ValidationResult";
import { validateConfirmPassword, validateFilled, validatePassword } from "../validation/AccountInputValidator";
import ValidatedInputField from "../../../common/component/input/ValidatedInputField";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import Button from "../../../common/component/input/Button";
import NotificationService from "../../../common/js/notification/NotificationService";
import InputField from "../../../common/component/input/InputField";
import Constants from "../../../common/js/Constants";
import { ACCOUNT_CHANGE_PASSWORD } from "../../../common/js/dao/endpoints/UserEndpoints";

const PasswordChanger = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newPassword, setNewPassword] = useState("");
    const [confirmPasword, setConfirmPassword] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");
    const [deactivateAllSessions, setDeactivateAllSessions] = useState(false);

    const [newPasswordValidationResult, setNewPasswordValidationResult] = useState(new ValidationResult(false));
    const [confirmPasswordValidationResult, setConfirmPasswordValidationResult] = useState(new ValidationResult(true));
    const [currentPasswordValidationResult, setCurrentPasswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setNewPasswordValidationResult(validatePassword(newPassword)), [newPassword]);
    useEffect(() => setConfirmPasswordValidationResult(validateConfirmPassword(confirmPasword, newPassword)), [confirmPasword, newPassword]);
    useEffect(() => setCurrentPasswordValidationResult(validateFilled(currentPassword)), [currentPassword]);

    const changePassword = async () => {
        const payload = {
            newPassword: newPassword,
            password: currentPassword,
            deactivateAllSessions: deactivateAllSessions
        }

        await ACCOUNT_CHANGE_PASSWORD.createRequest(payload)
            .send();

        const successMessage = localizationHandler.get("password-changed");

        if (deactivateAllSessions) {
            sessionStorage.successText = successMessage;
            window.location.href = Constants.INDEX_PAGE;
        } else {
            setNewPassword("");
            setConfirmPassword("");
            setCurrentPassword("");
            NotificationService.showSuccess(successMessage);
        }
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">{localizationHandler.get("tab-title")}</div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("new-password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-password-new-password-input-validation"
                                validationResult={newPasswordValidationResult}
                                inputField={<InputField
                                    id="account-change-password-new-password-input"
                                    placeholder={localizationHandler.get("new-password")}
                                    value={newPassword}
                                    onchangeCallback={setNewPassword}
                                    type="password"
                                />}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("confirm-password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-password-confirm-password-input-validation"
                                validationResult={confirmPasswordValidationResult}
                                inputField={<InputField
                                    id="account-change-password-confirm-password-input"
                                    placeholder={localizationHandler.get("confirm-password")}
                                    value={confirmPasword}
                                    onchangeCallback={setConfirmPassword}
                                    type="password"
                                />}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("current-password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-password-current-password-input-validation"
                                validationResult={currentPasswordValidationResult}
                                inputField={<InputField
                                    id="account-change-password-current-password-input"
                                    placeholder={localizationHandler.get("current-password")}
                                    value={currentPassword}
                                    onchangeCallback={setCurrentPassword}
                                    type="password"
                                />}

                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("deactivate-all-sessions")}
                            input={<InputField
                                id="account-change-password-deactivate-all-sessions"
                                type="checkbox"
                                onchangeCallback={setDeactivateAllSessions}
                                checked={deactivateAllSessions}
                            />}
                        />
                    </div>

                    <Button
                        id="account-change-password-button"
                        className="account-save-button"
                        label={localizationHandler.get("save")}
                        disabled={!newPasswordValidationResult.valid || !confirmPasswordValidationResult.valid || !currentPasswordValidationResult.valid}
                        onclick={changePassword}
                    />
                </div>
            </div>
        </div>
    );
}

export default PasswordChanger;