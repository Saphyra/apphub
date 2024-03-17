import React, { useEffect, useState } from "react";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import validate from "../../../common/js/validation/Validator";
import ValidatedField from "../../../common/js/validation/ValidatedField";
import ValidatedInputField from "../../../common/component/input/ValidatedInputField";
import Button from "../../../common/component/input/Button";
import register from "../controller/RegistrationController";
import InputField from "../../../common/component/input/InputField";

const RegistrationForm = ({ localizationHandler }) => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [validationResult, setValidationResult] = useState({});

    useEffect(() => runValidation(), [username, email, password, confirmPassword]);

    const runValidation = () => {
        const fields = {};
        fields[ValidatedField.USERNAME] = username;
        fields[ValidatedField.EMAIL] = email;
        fields[ValidatedField.PASSWORD] = password;
        fields[ValidatedField.CONFIRM_PASSWORD] = confirmPassword;

        setValidationResult(validate(fields, localizationHandler));
    }

    const isFormValid = () => {
        return Object.values(validationResult)
            .every((validation) => validation.valid);
    }

    return (
        <div className="registration-form">
            <div className="registration-form-content">
                <h2> {localizationHandler.get("registration")}</h2>

                <div className="registration-form-inputs">
                    <PreLabeledInputField
                        label={localizationHandler.get("username") + ":"}
                        input={
                            <ValidatedInputField
                                validationResultId="registration-username-validation"
                                validationResult={validationResult[ValidatedField.USERNAME]}
                                inputField={<InputField
                                    id="registration-username"
                                    placeholder={localizationHandler.get("username")}
                                    onchangeCallback={setUsername}
                                    value={username}
                                />}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("email-address") + ":"}
                        input={
                            <ValidatedInputField
                                validationResultId="registration-email-validation"
                                validationResult={validationResult[ValidatedField.EMAIL]}
                                inputField={<InputField
                                    id="registration-email"
                                    type="email"
                                    placeholder={localizationHandler.get("email-address")}
                                    onchangeCallback={setEmail}
                                    value={email}
                                />}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("password") + ":"}
                        input={
                            <ValidatedInputField
                                validationResultId="registration-password-validation"
                                validationResult={validationResult[ValidatedField.PASSWORD]}
                                inputField={<InputField
                                    id="registration-password"
                                    type="password"
                                    placeholder={localizationHandler.get("password")}
                                    onchangeCallback={setPassword}
                                    value={password}
                                />}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("confirm-password") + ":"}
                        input={
                            <ValidatedInputField
                                validationResultId="registration-confirm-password-validation"
                                validationResult={validationResult[ValidatedField.CONFIRM_PASSWORD]}
                                inputField={<InputField
                                    id="registration-confirm-password"
                                    type="password"
                                    placeholder={localizationHandler.get("confirm-password")}
                                    onchangeCallback={setConfirmPassword}
                                    value={confirmPassword}
                                />}
                            />
                        }
                    />
                </div>

                <Button
                    id="registration-button"
                    label={localizationHandler.get("register")}
                    onclick={() => register(username, email, password)}
                    disabled={!isFormValid()}
                />
            </div>
        </div>
    );
}

export default RegistrationForm;