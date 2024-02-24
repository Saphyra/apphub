import React, { useEffect, useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import Endpoints from "../../../../../../common/js/dao/dao";
import EventName from "../../../../../../common/js/event/EventName";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Event from "../../../../../../common/js/event/Event";
import validatePinGroupName from "../../../../common/validator/PinGroupNameValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../../../common/component/input/InputField";
import ConfirmationDialog from "../../../../../../common/component/confirmation_dialog/ConfirmationDialog";
import Stream from "../../../../../../common/js/collection/Stream";
import PinnedItem from "./PinnedItem";

const PinGroup = ({ pinGroupId, pinGroupName, localizationHandler, setLastEvent, setConfirmationDialogData, setPinGroups, lastEvent }) => {
    const [contentEditingEnabled, setContentEditingEnabled] = useState(false);
    const [newPinGroupName, setNewPinGroupName] = useState(pinGroupName);
    const [displayItems, setDisplayItems] = useState(false);
    const [items, setItems] = useState([]);

    useEffect(() => loadItems(), [displayItems]);
    const loadItems = () => {
        if (!displayItems) {
            return;
        }

        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_PINNED_ITEMS.createRequest(null, null, { pinGroupId: pinGroupId })
                .send();
            setItems(response);
        }
        fetch();
    }

    useEffect(() => processEvent(), [lastEvent]);
    const processEvent = () => {
        if (lastEvent === null) {
            return;
        }

        switch (lastEvent.eventName) {
            case EventName.NOTEBOOK_LIST_ITEM_PINNED:
                loadItems();
                break;
            case EventName.NOTEBOOK_PINNED_ITEM_MOVED:
                if (lastEvent.payload.pinGroupId === pinGroupId) {
                    setItems(lastEvent.payload.items);
                }
                break;
        }
    }

    const openDeleteConfirmationDialog = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "confirm-pin-group-deletion",
            localizationHandler.get("confirm-pin-group-deletion-title"),
            localizationHandler.get("confirm-pin-group-deletion-content", { pinGroupName: pinGroupName }),
            [
                <Button
                    key="delete"
                    id="notebook-delete-pin-group-button"
                    label={localizationHandler.get("delete-pin-group")}
                    onclick={deleteGroup}
                />,
                <Button
                    key="cancel"
                    id="notebook-delete-pin-group-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );
        setConfirmationDialogData(confirmationDialogData);
    }

    const deleteGroup = async () => {
        const response = await Endpoints.NOTEBOOK_DELETE_PIN_GROUP.createRequest(null, { pinGroupId: pinGroupId })
            .send();

        setPinGroups(response);
        setLastEvent(new Event(EventName.NOTEBOOK_PIN_GROUP_MODIFIED));
        setConfirmationDialogData(null);
    }

    const rename = async () => {
        const validationResult = validatePinGroupName(newPinGroupName);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        const response = await Endpoints.NOTEBOOK_RENAME_PIN_GROUP.createRequest({ value: newPinGroupName }, { pinGroupId: pinGroupId })
            .send();

        setPinGroups(response);
        setLastEvent(new Event(EventName.NOTEBOOK_PIN_GROUP_MODIFIED));
        setContentEditingEnabled(false);
    }

    const getItems = () => {
        return new Stream(items)
            .sorted((a, b) => a.title.localeCompare(b.title))
            .map(listItem => <PinnedItem
                key={listItem.id}
                listItem={listItem}
                localizationHandler={localizationHandler}
                setLastEvent={setLastEvent}
                pinGroupId={pinGroupId}
                setItems={setItems}
            />)
            .toList();
    }

    return (
        <div className="notebook-pin-group">
            <div className="notebook-pin-group-content">
                <span className="notebook-pin-group-name">{pinGroupName}</span>

                <div className="notebook-pin-group-buttons">
                    <Button
                        className="notebook-pin-group-rename-button"
                        title={localizationHandler.get("rename")}
                        onclick={() => setContentEditingEnabled(true)}
                    />
                    <Button
                        className="notebook-pin-group-delete-button"
                        title={localizationHandler.get("delete")}
                        onclick={openDeleteConfirmationDialog}
                    />
                </div>
            </div>

            {displayItems &&
                <div className="notebook-pin-group-items">
                    {getItems()}
                </div>
            }

            <div className="centered">
                <Button
                    className="notebook-pin-group-display-items-toggle-button"
                    label={displayItems ? "^" : "v"}
                    onclick={() => setDisplayItems(!displayItems)}
                />
            </div>

            {contentEditingEnabled &&
                <ConfirmationDialog
                    id="rename-pin-group"
                    title={localizationHandler.get("rename-pin-group-title")}
                    content={<PreLabeledInputField
                        label={localizationHandler.get("pin-group-name")}
                        input={<InputField
                            id="notebook-rename-pin-group-input"
                            value={newPinGroupName}
                            onchangeCallback={setNewPinGroupName}
                            placeholder={localizationHandler.get("pin-group-name")}
                        />}
                    />}
                    choices={[
                        <Button
                            key="rename"
                            id="notebook-pin-group-rename-button"
                            label={localizationHandler.get("rename")}
                            onclick={rename}
                        />,
                        <Button
                            key="cancel"
                            id="notebook-pin-group-rename-cancel-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setContentEditingEnabled(false)}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default PinGroup;