import React, { useEffect, useState } from "react";
import PreLabeledInputField from "../../common/component/input/PreLabeledInputField";
import validate from "../../common/js/validation/Validator";
import ValidatedField from "../../common/js/validation/ValidatedField";
import ValidatedInputField from "../../common/component/input/ValidatedInputField";
import Button from "../../common/component/input/Button";
import register from "./controller/RegistrationController";

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
                                validationResult={validationResult[ValidatedField.USERNAME]}
                                type="text"
                                placeholder={localizationHandler.get("username")}
                                onchangeCallback={setUsername}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("email-address") + ":"}
                        input={
                            <ValidatedInputField
                                validationResult={validationResult[ValidatedField.EMAIL]}
                                type="text"
                                placeholder={localizationHandler.get("email-address")}
                                onchangeCallback={setEmail}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("password") + ":"}
                        input={
                            <ValidatedInputField
                                validationResult={validationResult[ValidatedField.PASSWORD]}
                                type="password"
                                placeholder={localizationHandler.get("password")}
                                onchangeCallback={setPassword}
                            />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("confirm-password") + ":"}
                        input={
                            <ValidatedInputField
                                validationResult={validationResult[ValidatedField.CONFIRM_PASSWORD]}
                                type="password"
                                placeholder={localizationHandler.get("confirm-password")}
                                onchangeCallback={setConfirmPassword}
                            />
                        }
                    />
                </div>

                <Button
                    label={localizationHandler.get("register")}
                    onclick={() => register(username, email, password)}
                    disabled={!isFormValid()}
                />
            </div>
        </div>
    );
}

export default RegistrationForm;