import React, { useEffect, useState } from "react";
import Settings from "./settings/Settings";
import "./search.css";
import EventName from "../../../../../../common/js/event/EventName";
import Stream from "../../../../../../common/js/collection/Stream";
import ListItem from "../../list_item/ListItem";
import ListItemMode from "../../list_item/ListItemMode";
import UserSettings from "../../../../common/UserSettings";
import { useUpdateEffect } from "react-use";
import useHasFocus from "../../../../../../common/hook/UseHasFocus";
import compareListItems from "./ListItemComparator";
import { NOTEBOOK_SEARCH } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import CategoryNavigation from "./navigation/CategoryNavigation";

const Search = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    lastEvent,
    setLastEvent,
    userSettings,
    changeUserSettings,
    setConfirmationDialogData
}) => {
    const [searchResult, setSearchResult] = useState([]);
    const [selectedItems, setSelectedItems] = useState([]);

    useEffect(() => loadSearchResult(), [openedListItem.id]);
    useEffect(() => processEvent(), [lastEvent]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            loadSearchResult();
        }
    }, [isInFocus]);

    const loadSearchResult = () => {
        const fetch = async () => {
            const response = await NOTEBOOK_SEARCH.createRequest({ value: openedListItem.id })
                .send();

            setSearchResult(response);
        }
        fetch();
    }

    const processEvent = () => {
        if (lastEvent === null) {
            return;
        }

        switch (lastEvent.eventName) {
            case EventName.NOTEBOOK_LIST_ITEM_DELETED:
            case EventName.NOTEBOOK_LIST_ITEM_ARCHIVED:
            case EventName.NOTEBOOK_LIST_ITEM_PINNED:
            case EventName.NOTEBOOK_LIST_ITEM_CLONED:
                loadSearchResult();
                break;
        }
    }

    const getContent = () => {
        if (searchResult.length === 0) {
            return (
                <div id="notebook-content-search-content-empty">
                    {localizationHandler.get("no-search-result")}
                </div>
            );
        }

        return new Stream(searchResult)
            .sorted((a, b) => compareListItems(a, b))
            .filter(child => userSettings[UserSettings.SHOW_ARCHIVED] || !child.archived)
            .map(child =>
                <ListItem
                    key={child.id}
                    localizationHandler={localizationHandler}
                    data={child}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    listItemMode={ListItemMode.SEARCH_RESULT}
                    setConfirmationDialogData={setConfirmationDialogData}
                    selectedItems={selectedItems}
                    setSelectedItems={setSelectedItems}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-content-search" className="notebook-content">
            <Settings
                localizationHandler={localizationHandler}
                openedListItem={openedListItem}
                setOpenedListItem={setOpenedListItem}
                userSettings={userSettings}
                changeUserSettings={changeUserSettings}
            />

            <CategoryNavigation
                openedListItem={openedListItem}
                title={localizationHandler.get("search-result")}
                parent={openedListItem.parent}
                setOpenedListItem={setOpenedListItem}
                listItems={searchResult}
                setSelectedItems={setSelectedItems}
                selectedItems={selectedItems}
                setLastEvent={setLastEvent}
                setConfirmationDialogData={setConfirmationDialogData}
            />

            <div id="notebook-category-content-list">
                {getContent()}
            </div>
        </div>
    );
}

export default Search;