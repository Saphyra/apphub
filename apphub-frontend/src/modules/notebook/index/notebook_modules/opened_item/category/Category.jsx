import React, { useEffect, useState } from "react";
import "./category.css"
import Button from "../../../../../../common/component/input/Button";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import ListItemType from "../../../../common/ListItemType";
import ListItem from "../../list_item/ListItem";
import Settings from "./settings/Settings";
import EventName from "../../../../../../common/js/event/EventName";
import ListItemMode from "../../list_item/ListItemMode";
import moveListItem from "../../../../common/MoveListItemService";
import UserSettings from "../../../../common/UserSettings";

const Category = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    lastEvent,
    setLastEvent,
    userSettings,
    changeUserSettings
}) => {
    const [openedCategoryContent, setOpenedCategoryContent] = useState({ children: [] });

    useEffect(() => processEvent(), [lastEvent]);
    useEffect(() => loadCategory(), [openedListItem]);

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
                loadCategory();
                break;
        }
    }

    const loadCategory = () => {
        const fetch = async () => {
            const queryParams = openedListItem.id === null ? null : { categoryId: openedListItem.id };
            const response = await Endpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY.createRequest(null, null, queryParams)
                .send();
            setOpenedCategoryContent(response);
        }
        fetch();
    }

    const getContent = () => {
        if (openedCategoryContent.children.length === 0) {
            return (
                <div id="notebook-content-category-content-empty">
                    {localizationHandler.get("category-empty")}
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

        return new Stream(openedCategoryContent.children)
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
                />
            )
            .toList();
    }

    //Drag & Drop
    const handleOnDragOver = (e) => {
        if (openedListItem.id !== null) {
            e.preventDefault();
        }
    }

    const handleOnDrop = (e) => {
        const movedItemId = e.dataTransfer.getData("id");
        moveListItem(movedItemId, openedCategoryContent.parent, setLastEvent);
    }

    return (
        <div id="notebook-content-category" className="notebook-content">
            <Settings
                localizationHandler={localizationHandler}
                openedListItem={openedListItem}
                setOpenedListItem={setOpenedListItem}
                userSettings={userSettings}
                changeUserSettings={changeUserSettings}
            />

            <div id="notebook-content-category-content">
                <div
                    id="notebook-content-category-content-navigation"
                    onDrop={handleOnDrop}
                    onDragOver={handleOnDragOver}
                >
                    <div id="notebook-content-category-content-title"> {openedListItem.id === null ? localizationHandler.get("root") : openedCategoryContent.title} </div>

                    <Button
                        id="notebook-content-category-content-up-button"
                        label={localizationHandler.get("up")}
                        onclick={() => setOpenedListItem({ id: openedCategoryContent.parent, type: ListItemType.CATEGORY })}
                        disabled={openedListItem.id === null}
                    />
                </div>

                <div id="notebook-category-content-list">
                    {getContent()}
                </div>
            </div>
        </div>
    );
}

export default Category;