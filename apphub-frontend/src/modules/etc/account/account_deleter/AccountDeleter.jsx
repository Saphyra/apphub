import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./account_deleter_localization.json";
import { useEffect, useState } from "react";
import ValidationResult from "common/js/validation/ValidationResult";
import { validateFilled } from "../validation/AccountInputValidator";
import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "common/component/input/Button";
import { ACCOUNT_DELETE_ACCOUNT } from "common/js/dao/endpoints/UserEndpoints";
import Constants from "common/js/Constants";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import ValidatedInputField from "common/component/input/ValidatedInputField";
import InputField from "common/component/input/InputField";

const AccountDeleter = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [password, setPassword] = useState("");

    const [passwordValidationResult, setPassswordValidationResult] = useState(new ValidationResult(false));

    useEffect(() => setPassswordValidationResult(validateFilled(password)), [password]);

    const confirmDeleteAccount = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "account-delete-account-confirmation-dialog",
            localizationHandler.get("confirm-deletion-title"),
            localizationHandler.get("confirm-deletion-content"),
            [
                <Button
                    key="delete"
                    id="account-delete-account-confirm-button"
                    label={localizationHandler.get("delete-account")}
                    onclick={deleteAccount}
                />,
                <Button
                    key="cancel"
                    id="account-delete-account-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );
        setConfirmationDialogData(confirmationDialogData);
    }

    const deleteAccount = async () => {
        try {
            await ACCOUNT_DELETE_ACCOUNT.createRequest({ value: password })
                .send();

            sessionStorage.successText = localizationHandler.get("account-deleted");

            window.location.href = Constants.INDEX_PAGE;
        } finally {
            setConfirmationDialogData(null);
            setPassword("");
        }
    }

    return (
        <div className="account-tab-wrapper">
            <div className="account-tab">
                <div className="account-tab-title">{localizationHandler.get("tab-title")}</div>
                <div className="account-tab-content">
                    <div>
                        <PreLabeledInputField
                            label={localizationHandler.get("password")}
                            input={<ValidatedInputField
                                validationResultId="account-delete-account-password-input-validation"
                                validationResult={passwordValidationResult}
                                inputField={<InputField
                                    id="account-delete-account-password-input"
                                    placeholder={localizationHandler.get("password")}
                                    value={password}
                                    onchangeCallback={setPassword}
                                    type="password"
                                />}
                            />}
                        />
                    </div>

                    <Button
                        id="account-delete-account-button"
                        className="account-save-button"
                        label={localizationHandler.get("delete-account")}
                        disabled={!passwordValidationResult.valid}
                        onclick={confirmDeleteAccount}
                    />
                </div>
            </div>
        </div>
    );
}

export default AccountDeleter;