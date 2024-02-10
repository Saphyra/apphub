import React, { useEffect, useState } from "react";
import localizationData from "./password_changer_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import ValidationResult from "../../../common/js/validation/ValidationResult";
import { validateConfirmPassword, validateFilled, validatePassword } from "../validation/AccountInputValidator";
import ValidatedInputField from "../../../common/component/input/ValidatedInputField";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import Button from "../../../common/component/input/Button";
import Endpoints from "../../../common/js/dao/dao";
import NotificationService from "../../../common/js/notification/NotificationService";

const PasswordChanger = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newPassword, setNewPassword] = useState("");
    const [confirmPasword, setConfirmPassword] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");

    const [newPasswordValidationResult, setNewPasswordValidationResult] = useState(new ValidationResult(false));
    const [confirmPasswordValidationResult, setConfirmPasswordValidationResult] = useState(new ValidationResult(true));
    const [currentPasswordValidationResult, setCurrentPasswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setNewPasswordValidationResult(validatePassword(newPassword)), [newPassword]);
    useEffect(() => setConfirmPasswordValidationResult(validateConfirmPassword(confirmPasword, newPassword)), [confirmPasword, newPassword]);
    useEffect(() => setCurrentPasswordValidationResult(validateFilled(currentPassword)), [currentPassword]);

    const changePassword = async () => {
        await Endpoints.ACCOUNT_CHANGE_PASSWORD.createRequest({ newPassword: newPassword, password: currentPassword })
            .send();

        setNewPassword("");
        setConfirmPassword("");
        setCurrentPassword("");

        NotificationService.showSuccess(localizationHandler.get("password-changed"));
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
                                inputId="account-change-password-new-password-input"
                                placeholder={localizationHandler.get("new-password")}
                                value={newPassword}
                                onchangeCallback={setNewPassword}
                                validationResult={newPasswordValidationResult}
                                type="password"
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("confirm-password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-password-confirm-password-input-validation"
                                inputId="account-change-password-confirm-password-input"
                                placeholder={localizationHandler.get("confirm-password")}
                                value={confirmPasword}
                                onchangeCallback={setConfirmPassword}
                                validationResult={confirmPasswordValidationResult}
                                type="password"
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("current-password")}
                            input={<ValidatedInputField
                                validationResultId="account-change-password-current-password-input-validation"
                                inputId="account-change-password-current-password-input"
                                placeholder={localizationHandler.get("current-password")}
                                value={currentPassword}
                                onchangeCallback={setCurrentPassword}
                                validationResult={currentPasswordValidationResult}
                                type="password"
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