import { useState } from "react";
import Button from "../../../common/component/input/Button";
import InputField from "../../../common/component/input/InputField";
import PostLabeledInputField from "../../../common/component/input/PostLabeledInputField";
import login from "../controller/LoginController";

const LoginForm = ({ localizationHandler }) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [rememberMe, setRememberMe] = useState(false);

    const rememberMeInput = <InputField
        type="checkbox"
        onchangeCallback={setRememberMe}
    />

    return (
        <div className="login-form">
            <InputField
                type="text"
                placeholder={localizationHandler.get("email-address")}
                onchangeCallback={setEmail}
            />

            <InputField
                type="password"
                placeholder={localizationHandler.get("password")}
                onchangeCallback={setPassword}
            />

            <PostLabeledInputField
                label={localizationHandler.get("remember-me")}
                input={rememberMeInput}

            />

            <Button
                label={localizationHandler.get("login-button")}
                onclick={() => login(email, password, rememberMe)}
            />
        </div>
    );
}

export default LoginForm;