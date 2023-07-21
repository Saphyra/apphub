import React, { useEffect, useState } from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import "./settings.css"
import ListItemType from "../../../../../common/ListItemType";
import UserSettingsDropdown from "./user/UserSettingsDropdown";

const Settings = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    userSettings,
    changeUserSettings
}) => {
    const [settingsDisplayed, setSettingsDispalyed] = useState(false);
    const [searchText, setSearchText] = useState(openedListItem.type === ListItemType.SEARCH ? openedListItem.id : "");
    const [searchTimeout, setSearchTimeout] = useState(null);

    let timeout = null;

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
        if (searchTimeout) {
            clearTimeout(searchTimeout);
            setSearchTimeout(null);
        }

        if (searchText.length < 3) {
            return;
        }

        timeout = setTimeout(
            () => {
                setOpenedListItem({
                    id: searchText,
                    type: ListItemType.SEARCH,
                    parent: openedListItem.type === ListItemType.SEARCH ? openedListItem.parent : openedListItem.id
                })
            },
            1000
        );

        setSearchTimeout(timeout);
    }

    return (
        <div id="notebook-settings">
            <InputField
                id="notebook-search"
                type="text"
                placeholder={localizationHandler.get("search")}
                onchangeCallback={setSearchText}
                value={searchText}
                autoFocus={openedListItem.type === ListItemType.SEARCH}
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