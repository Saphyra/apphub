import Button from "common/component/input/Button";
import OpenedPageType from "modules/feature/notebook/common/OpenedPageType";
import ParentSelector from "modules/feature/notebook/common/parent_selector/ParentSelector";
import UserSettings from "modules/feature/notebook/common/UserSettings";
import { useState } from "react";

const DefaultListItemSelector = ({ localizationHandler, defaultListItemId, setDefaultListItemId, setEditingEnabled, changeUserSettings }) => {
    const [listItemId, setListItemId] = useState(defaultListItemId);

    return (
        <div>
            <ParentSelector
                parentId={listItemId}
                setParentId={setListItemId}
                onlyCategory={false}
                excludedListItemTypes={[OpenedPageType.LINK, OpenedPageType.ONLY_TITLE]}
            />

            <Button
                id="notebook-settings-default-list-item-selector-save"
                label={localizationHandler.get("save")}
                onclick={save}
            />

            <Button
                id="notebook-settings-default-list-item-selector-cancel"
                label={localizationHandler.get("cancel")}
                onclick={() => setEditingEnabled(false)}
            />
        </div>
    );

    async function save() {
        changeUserSettings(UserSettings.DEFAULT_LIST_ITEM_ID, listItemId);
        setEditingEnabled(false);
    }
}

export default DefaultListItemSelector;