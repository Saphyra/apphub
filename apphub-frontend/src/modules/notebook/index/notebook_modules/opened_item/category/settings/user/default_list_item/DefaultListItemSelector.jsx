import { useState } from "react";
import ParentSelector from "../../../../../../../common/parent_selector/ParentSelector";
import Button from "../../../../../../../../../common/component/input/Button";
import UserSettings from "../../../../../../../common/UserSettings";
import OpenedPageType from "../../../../../../../common/OpenedPageType";

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