import InputField from "common/component/input/InputField";
import PostLabeledInputField from "common/component/input/PostLabeledInputField";
import { UserSettings } from "modules/feature/calendar/common/UserSettings";
import DefaultListItem from "./default_list_item/DefaultListItem";
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

            <DefaultListItem
                localizationHandler={localizationHandler}
                userSettings={userSettings}
                changeUserSettings={changeUserSettings}
            />
        </div>
    )
}

export default UserSettingsDropdown;