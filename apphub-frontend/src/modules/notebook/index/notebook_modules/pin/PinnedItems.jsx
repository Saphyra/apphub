import React, { useEffect, useState } from "react";
import Stream from "../../../../../common/js/collection/Stream";
import EventName from "../../../../../common/js/event/EventName";
import ListItem from "../list_item/ListItem";
import ListItemMode from "../list_item/ListItemMode";
import UserSettings from "../../../common/UserSettings";
import useHasFocus from "../../../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import PinGroups from "./groups/PinGroups";
import { hasValue } from "../../../../../common/js/Utils";
import { NOTEBOOK_GET_PINNED_ITEMS } from "../../../../../common/js/dao/endpoints/NotebookEndpoints";

const PinnedItems = ({ localizationHandler, openedListItem, setOpenedListItem, lastEvent, setLastEvent, userSettings }) => {
    const [pinnedItems, setPinnedItems] = useState([]);
    const [pinGroupId, setPinGroupId] = useState(sessionStorage.pinGroupId || null);

    useEffect(() => loadPinnedItems(), [pinGroupId]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            loadPinnedItems();
        }
    }, [isInFocus]);

    useEffect(() => processEvent(), [lastEvent]);
    const processEvent = () => {
        if (lastEvent === null) {
            return;
        }

        switch (lastEvent.eventName) {
            case EventName.NOTEBOOK_LIST_ITEM_DELETED:
            case EventName.NOTEBOOK_LIST_ITEM_ARCHIVED:
            case EventName.NOTEBOOK_LIST_ITEM_PINNED:
            case EventName.NOTEBOOK_LIST_ITEM_CLONED:
            case EventName.NOTEBOOK_LIST_ITEM_MODIFIED:
                loadPinnedItems();
                break;
            case EventName.NOTEBOOK_PINNED_ITEM_MOVED:
                if (lastEvent.payload.pinGroupId === pinGroupId) {
                    setPinnedItems(lastEvent.payload.items);
                }
                break;
        }
    }

    const loadPinnedItems = () => {
        const fetch = async () => {
            const response = await NOTEBOOK_GET_PINNED_ITEMS.createRequest(
                null,
                {},
                hasValue(pinGroupId) && pinGroupId !== "null" ? { pinGroupId: pinGroupId } : {}
            )
                .send();
            setPinnedItems(response);
        }
        fetch();
    }

    const updatePinGroupId = (newId) => {
        if (hasValue(newId)) {
            sessionStorage.pinGroupId = newId;
        } else {
            delete sessionStorage.pinGroupId;
        }

        setPinGroupId(newId);
    }

    const getPinnedItems = () => {
        return new Stream(pinnedItems)
            .sorted((a, b) => a.title.localeCompare(b.title))
            .filter(item => userSettings[UserSettings.SHOW_ARCHIVED] || !item.archived)
            .map(item => (
                <ListItem
                    key={item.id}
                    localizationHandler={localizationHandler}
                    data={item}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    listItemMode={ListItemMode.PINNED_ITEM}
                />
            ))
            .toList();
    }

    return (
        <div id="notebook-pinned-items">
            <PinGroups
                setPinGroupId={updatePinGroupId}
                pinGroupId={pinGroupId}
                openedListItem={openedListItem}
                setOpenedListItem={setOpenedListItem}
                lastEvent={lastEvent}
                setLastEvent={setLastEvent}
            />

            {getPinnedItems()}
        </div>
    );
}

export default PinnedItems;