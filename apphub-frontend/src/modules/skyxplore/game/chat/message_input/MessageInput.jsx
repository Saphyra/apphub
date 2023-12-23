import React, { useState } from "react";
import "./message_input.css";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./message_input_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";

const MessageInput = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [message, setMessage] = useState("");

    const sendIfEnter = (e) => {
        if (e.which === 13) {
            //TODO send message
        }
    }

    return (
        <div id="skyxplore-game-chat-message-input-container">
            <InputField
                type="text"
                id="skyxplore-game-chat-message-input"
                onchangeCallback={setMessage}
                onkeyupCallback={sendIfEnter}
                placeholder={localizationHandler.get("placeholder")}
            />
        </div>
    );
}

export default MessageInput;