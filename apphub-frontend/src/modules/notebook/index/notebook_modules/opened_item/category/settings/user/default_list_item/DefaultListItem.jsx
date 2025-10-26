import { useEffect, useState } from "react";
import useLoader from "../../../../../../../../../common/hook/Loader";
import UserSettings from "../../../../../../../common/UserSettings";
import { NOTEBOOK_GET_LIST_ITEM } from "../../../../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import Optional from "../../../../../../../../../common/js/collection/Optional";
import { hasValue } from "../../../../../../../../../common/js/Utils";
import Button from "../../../../../../../../../common/component/input/Button";
import DefaultListItemSelector from "./DefaultListItemSelector";

const DefaultListItem = ({ localizationHandler, userSettings, changeUserSettings }) => {
    const [defaultListItemId, setDefaultListItemId] = useState(null);
    const [defaultListItem, setDefaultListItem] = useState(null);
    const [editingEnabled, setEditingEnabled] = useState(false);

    useEffect(() => setDefaultListItemId(userSettings[UserSettings.DEFAULT_LIST_ITEM_ID]), [userSettings]);

    useLoader({
        request: NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: userSettings[UserSettings.DEFAULT_LIST_ITEM_ID] }),
        mapper: setDefaultListItem,
        listener: [defaultListItemId],
        condition: () => hasValue(defaultListItemId)
    });

    return (
        <div id="notebook-settings-default-list-item">
            <span>{localizationHandler.get("default-list-item-title")}</span>
            <span>: </span>
            <span id="notebook-settings-default-list-item-title">
                {new Optional(defaultListItem).map(dl => dl.title).orElseGet(() => localizationHandler.get("root"))}
            </span>

            {!editingEnabled &&
                <Button
                    id="notebook-settings-edit-default-list-item"
                    label={localizationHandler.get("edit")}
                    onclick={() => setEditingEnabled(true)}
                />
            }

            {editingEnabled &&
                <DefaultListItemSelector
                    localizationHandler={localizationHandler}
                    defaultListItemId={defaultListItemId}
                    setDefaultListItemId={setDefaultListItemId}
                    setEditingEnabled={setEditingEnabled}
                    changeUserSettings={changeUserSettings}
                />
            }
        </div>
    );
}

export default DefaultListItem;