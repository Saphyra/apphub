import React, { useState } from "react";
import OpenedListItemHeader from "../OpenedListItemHeader";
import useLoader from "../../../../../../common/hook/Loader";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../../../common/component/input/InputField";
import "./pin_group_manager.css";
import Button from "../../../../../../common/component/input/Button";
import validatePinGroupName from "../../../../common/validator/PinGroupNameValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import Event from "../../../../../../common/js/event/Event";
import EventName from "../../../../../../common/js/event/EventName";
import Stream from "../../../../../../common/js/collection/Stream";
import PinGroup from "./PinGroup";
import useHasFocus from "../../../../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import { NOTEBOOK_CREATE_PIN_GROUP, NOTEBOOK_GET_PIN_GROUPS } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";

const PinGroupManager = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData, lastEvent, setDisplaySpinner }) => {
    const [pinGroups, setPinGroups] = useState([]);
    const [newPinGroupName, setNewPinGroupName] = useState("");
    const [loadPinGroupsTrigger, setLoadPinGroupsTrigger] = useState(0);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            setLoadPinGroupsTrigger(loadPinGroupsTrigger + 1);
        }
    }, [isInFocus]);

    useLoader({ request: NOTEBOOK_GET_PIN_GROUPS.createRequest(), mapper: setPinGroups, listener: [loadPinGroupsTrigger] });

    const createPinGroup = async () => {
        const validationResult = validatePinGroupName(newPinGroupName);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        const response = await NOTEBOOK_CREATE_PIN_GROUP.createRequest({ value: newPinGroupName })
            .send(setDisplaySpinner);

        setNewPinGroupName("");
        setPinGroups(response);
        setLastEvent(new Event(EventName.NOTEBOOK_PIN_GROUP_MODIFIED));
    }

    const getPinGroups = () => {
        return new Stream(pinGroups)
            .map(pinGroup => <PinGroup
                key={pinGroup.pinGroupId}
                pinGroupName={pinGroup.pinGroupName}
                pinGroupId={pinGroup.pinGroupId}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                setLastEvent={setLastEvent}
                setPinGroups={setPinGroups}
                lastEvent={lastEvent}
                setDisplaySpinner={setDisplaySpinner}
            />)
            .toList();
    }

    return (
        <div id="notebook-pin-group-manager" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={localizationHandler.get("pin-group-manager")}
                editingEnabled={false}
                close={() => setOpenedListItem(openedListItem.parent)}
            />

            <div
                id="notebook-pin-group-manager-groups"
                className="notebook-content-view-main"
            >
                {getPinGroups()}
            </div>

            <div className="notebook-content-buttons">
                <PreLabeledInputField
                    className="notebook-new-pin-group-label"
                    label={localizationHandler.get("create-pin-group")}
                    input={<InputField
                        id="notebook-new-pin-group-title"
                        placeholder={localizationHandler.get("new-pin-group-name")}
                        value={newPinGroupName}
                        onchangeCallback={setNewPinGroupName}
                    />}
                />

                <Button
                    id="notebook-new-pin-group-create-button"
                    label={localizationHandler.get("create")}
                    onclick={createPinGroup}
                />
            </div>
        </div>
    );
}

export default PinGroupManager;