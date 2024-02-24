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
        id="login-remember-me"
        type="checkbox"
        onchangeCallback={setRememberMe}
        checked={rememberMe}
    />

    const loginIfEnter = (e) => {
        if (e.which === 13) {
            login(email, password, rememberMe);
        }
    }

    return (
        <div className="login-form">
            <InputField
                id="login-email"
                type="text"
                placeholder={localizationHandler.get("email-address")}
                onchangeCallback={setEmail}
                onkeyupCallback={loginIfEnter}
            />

            <InputField
                id="login-password"
                type="password"
                placeholder={localizationHandler.get("password")}
                onchangeCallback={setPassword}
                onkeyupCallback={loginIfEnter}
            />

            <PostLabeledInputField
                label={localizationHandler.get("remember-me")}
                input={rememberMeInput}

            />

            <Button
                id="login-button"
                label={localizationHandler.get("login-button")}
                onclick={() => login(email, password, rememberMe)}
            />
        </div>
    );
}

export default LoginForm;