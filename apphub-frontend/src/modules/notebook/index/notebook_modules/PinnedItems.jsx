import React, { useEffect, useState } from "react";
import Endpoints from "../../../../common/js/dao/dao";
import Stream from "../../../../common/js/collection/Stream";
import EventName from "../../../../common/js/event/EventName";
import ListItem from "./list_item/ListItem";
import ListItemMode from "./list_item/ListItemMode";
import UserSettings from "../../common/UserSettings";
import useHasFocus from "../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";

const PinnedItems = ({ localizationHandler, setOpenedListItem, lastEvent, setLastEvent, userSettings }) => {
    const [pinned, setPinned] = useState([]);

    useEffect(() => loadPinnedItems(), []);
    useEffect(() => processEvent(), [lastEvent]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            loadPinnedItems();
        }
    }, [isInFocus]);

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
        }
    }

    const loadPinnedItems = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_PINNED_ITEMS.createRequest()
                .send();
            setPinned(response);
        }
        fetch();
    }

    const getPinnedItems = () => {
        return new Stream(pinned)
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
            {getPinnedItems()}
        </div>
    );
}

export default PinnedItems;