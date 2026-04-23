import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./pin_groups_localization.json";
import "./pin_groups.css";
import { useEffect, useState } from "react";
import { useUpdateEffect } from "react-use";
import { hasValue } from "common/js/Utils";
import Stream from "common/js/collection/Stream";
import useHasFocus from "common/hook/UseHasFocus";
import useLoader from "common/hook/Loader";
import EventName from "common/js/event/EventName";
import OpenedPageType from "modules/feature/notebook/common/OpenedPageType";
import Button from "common/component/input/Button";
import Event from "common/js/event/Event";
import { NOTEBOOK_ADD_ITEM_TO_PIN_GROUP, NOTEBOOK_GET_PIN_GROUPS, NOTEBOOK_PIN_GROUP_OPENED } from "modules/feature/notebook/NotebookEndpoints";

const PinGroups = ({ pinGroupId, setPinGroupId, openedListItem, setOpenedListItem, lastEvent, setLastEvent, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [pinGroups, setPinGroups] = useState([]);
    const [loadPinGroupsTrigger, setLoadPinGroupsTrigger] = useState(0);

    useEffect(() => processEvent(), [lastEvent]);
    useUpdateEffect(() => {
        if (!hasValue(pinGroupId)) {
            return;
        }

        const pinGroupExists = new Stream(pinGroups)
            .anyMatch(pinGroup => pinGroup.pinGroupId === pinGroupId);
        if (!pinGroupExists) {
            setPinGroupId(null);
        }
    }, [pinGroups]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            setLoadPinGroupsTrigger(loadPinGroupsTrigger + 1);
        }
    }, [isInFocus]);

    useLoader({ request: NOTEBOOK_GET_PIN_GROUPS.createRequest(), mapper: setPinGroups, listener: [loadPinGroupsTrigger] });

    const processEvent = () => {
        if (lastEvent === null) {
            return;
        }

        switch (lastEvent.eventName) {
            case EventName.NOTEBOOK_PIN_GROUP_MODIFIED:
                setLoadPinGroupsTrigger(loadPinGroupsTrigger + 1);
                break;
        }
    }

    const openCreatePinGroupDialog = () => {
        if (openedListItem.type === OpenedPageType.MANAGE_PIN_GROUPS) {
            return;
        }

        setOpenedListItem({ type: OpenedPageType.MANAGE_PIN_GROUPS, parent: openedListItem })
    }

    const getCustomGroups = () => {
        return new Stream(pinGroups)
            .sorted((a, b) => b.lastOpened.localeCompare(a.lastOpened))
            .map(pinGroup => <PinGroup
                key={pinGroup.pinGroupId}
                selectedPinGroupId={pinGroupId}
                setPinGroupId={setOpened}
                pinGroupId={pinGroup.pinGroupId}
                pinGroupName={pinGroup.pinGroupName}
                setLastEvent={setLastEvent}
            />)
            .toList();
    }

    const setOpened = async (pinGroupId) => {
        if (hasValue(pinGroupId)) {
            const response = await NOTEBOOK_PIN_GROUP_OPENED.createRequest(null, { pinGroupId: pinGroupId })
                .send(setDisplaySpinner);
            setPinGroups(response);
        }

        setPinGroupId(pinGroupId);
    }

    return (
        <div id="notebook-pin-groups">
            <Button
                id="notebook-manage-pin-groups-button"
                label="."
                title={localizationHandler.get("manage-pin-groups")}
                onclick={openCreatePinGroupDialog}
            />

            <PinGroup
                selectedPinGroupId={pinGroupId}
                setPinGroupId={setPinGroupId}
                pinGroupId={null}
                pinGroupName={localizationHandler.get("show-all")}
                setLastEvent={setLastEvent}
                setDisplaySpinner={setDisplaySpinner}
            />

            {getCustomGroups()}
        </div>
    );
}

const PinGroup = ({ selectedPinGroupId, setPinGroupId, pinGroupId, pinGroupName, setLastEvent, setDisplaySpinner }) => {
    //Drag & Drop
    const handleOnDragOver = (e) => {
        if (hasValue(pinGroupId)) {
            e.preventDefault();
        }
    }

    const handleOnDrop = async (e) => {
        const pinnedItemId = e.dataTransfer.getData("id");

        const response = await NOTEBOOK_ADD_ITEM_TO_PIN_GROUP.createRequest(null, { pinGroupId: pinGroupId, listItemId: pinnedItemId })
            .send(setDisplaySpinner);

        setLastEvent(new Event(
            EventName.NOTEBOOK_PINNED_ITEM_MOVED,
            {
                pinGroupId: pinGroupId,
                items: response
            }
        ));
    }

    return <span
        className="notebook-pin-group-wrapper"
        onDrop={handleOnDrop}
        onDragOver={handleOnDragOver}
    >
        <Button
            className={"notebook-pin-group" + (selectedPinGroupId === pinGroupId ? " notebook-selected-pin-group" : "")}
            label={pinGroupName}
            onclick={() => setPinGroupId(pinGroupId)}
        />
    </span>
}

export default PinGroups;