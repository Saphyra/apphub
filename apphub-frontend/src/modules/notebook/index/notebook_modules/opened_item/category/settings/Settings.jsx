import React, { useEffect, useState } from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import "./settings.css"
import OpenedPageType from "../../../../../common/OpenedPageType";
import UserSettingsDropdown from "./user/UserSettingsDropdown";

const Settings = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    userSettings,
    changeUserSettings
}) => {
    const [settingsDisplayed, setSettingsDispalyed] = useState(false);
    const [searchText, setSearchText] = useState(openedListItem.type === OpenedPageType.SEARCH ? openedListItem.id : "");

    useEffect(() => searchTextModified(), [searchText]);

    const getToggleSettingsDisplayedButton = () => {
        return <Button
            id="notebook-toggle-display-settings-button"
            onclick={() => setSettingsDispalyed(!settingsDisplayed)}
            label="V"
            className={settingsDisplayed ? "active" : ""}
        />
    }

    const searchTextModified = () => {
        const timeout = setTimeout(
            () => {
                if (searchText.length < 3) {
                    return;
                }

                setOpenedListItem({
                    id: searchText,
                    type: OpenedPageType.SEARCH,
                    parent: openedListItem.type === OpenedPageType.SEARCH ? openedListItem.parent : openedListItem.id
                })
            },
            1000
        );

        return () => clearTimeout(timeout);
    }

    return (
        <div id="notebook-settings">
            <InputField
                id="notebook-search"
                type="text"
                placeholder={localizationHandler.get("search")}
                onchangeCallback={setSearchText}
                value={searchText}
                autoFocus={openedListItem.type === OpenedPageType.SEARCH}
            />

            {getToggleSettingsDisplayedButton()}

            {settingsDisplayed &&
                <UserSettingsDropdown
                    localizationHandler={localizationHandler}
                    userSettings={userSettings}
                    changeUserSettings={changeUserSettings}
                />
            }
        </div>
    );
}

export default Settings;