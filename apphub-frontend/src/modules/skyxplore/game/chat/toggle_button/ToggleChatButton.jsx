import React from "react";
import Button from "../../../../../common/component/input/Button";
import "./toggle_chat_button.css";

const ToggleChatButton = ({ hasUnreadMessage, toggleCallback }) => {
    return <Button
        id="skyxplore-game-toggle-chat-button"
        className={hasUnreadMessage ? "unread" : ""}
        onclick={toggleCallback}
    />
}

export default ToggleChatButton;