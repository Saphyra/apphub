import React from "react";
import PostLabeledInputField from "../../../../../../../../common/component/input/PostLabeledInputField";
import InputField from "../../../../../../../../common/component/input/InputField";
import UserSettings from "../../../../../../common/UserSettings";
import "./user_settings.css";

const UserSettingsDropdown = ({
    userSettings,
    changeUserSettings,
    localizationHandler
}) => {
    return (
        <div id="notebook-settings-user">
            <PostLabeledInputField
                input={<InputField
                    id="notebook-settings-user-show-archived"
                    type="checkbox"
                    checked={userSettings[UserSettings.SHOW_ARCHIVED]}
                    onchangeCallback={(checked) => changeUserSettings(UserSettings.SHOW_ARCHIVED, checked)}
                />}
                label={localizationHandler.get("show-archived")}
            />
        </div>
    )
}

export default UserSettingsDropdown;