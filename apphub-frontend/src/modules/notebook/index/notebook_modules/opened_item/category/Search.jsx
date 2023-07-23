import React, { useEffect, useState } from "react";
import Settings from "./settings/Settings";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import "./search.css";
import EventName from "../../../../../../common/js/event/EventName";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import ListItem from "../../list_item/ListItem";
import ListItemMode from "../../list_item/ListItemMode";
import UserSettings from "../../../../common/UserSettings";

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

    useEffect(() => loadSearchResult(), [openedListItem.id]);
    useEffect(() => processEvent(), [lastEvent]);

    const loadSearchResult = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_SEARCH.createRequest({ value: openedListItem.id })
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

        const compare = (a, b) => {
            if (a.type === b.type) {
                return compareByTitle(a, b);
            }

            if (a.type === ListItemType.CATEGORY) {
                return -1;
            }

            return 1;

            function compareByTitle(a, b) {
                return a.title.localeCompare(b.title);
            }
        }

        return new Stream(searchResult)
            .sorted((a, b) => compare(a, b))
            .filter(child => userSettings[UserSettings.SHOW_ARCHIVED] || !child.archived)
            .map(child =>
                <ListItem
                    key={child.id}
                    localizationHandler={localizationHandler}
                    data={child}
                    setOpenedListItem={setOpenedListItem}
                    setLastEvent={setLastEvent}
                    listItemMode={ListItemMode.CATEGORY_CONTENT}
                    setConfirmationDialogData={setConfirmationDialogData}
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

            <div id="notebook-content-search-content">
                <div id="notebook-content-search-content-navigation">
                    <div id="notebook-content-search-content-title"> {localizationHandler.get("search-result")} </div>

                    <Button
                        id="notebook-content-search-content-up-button"
                        label={localizationHandler.get("up")}
                        onclick={() => setOpenedListItem({ id: openedListItem.parent, type: ListItemType.CATEGORY })}
                    />
                </div>

                <div id="notebook-category-content-list">
                    {getContent()}
                </div>
            </div>
        </div>
    );
}

export default Search;