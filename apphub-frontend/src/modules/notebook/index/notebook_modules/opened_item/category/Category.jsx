import React, { useEffect, useState } from "react";
import "./category.css"
import Button from "../../../../../../common/component/input/Button";
import Stream from "../../../../../../common/js/collection/Stream";
import OpenedPageType from "../../../../common/OpenedPageType";
import ListItem from "../../list_item/ListItem";
import Settings from "./settings/Settings";
import EventName from "../../../../../../common/js/event/EventName";
import ListItemMode from "../../list_item/ListItemMode";
import moveListItem from "../../../../common/MoveListItemService";
import UserSettings from "../../../../common/UserSettings";
import { useUpdateEffect } from "react-use";
import useHasFocus from "../../../../../../common/hook/UseHasFocus";
import compareListItems from "./ListItemComparator";
import { NOTEBOOK_GET_CHILDREN_OF_CATEGORY } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";

const Category = ({
    localizationHandler,
    openedListItem,
    setOpenedListItem,
    lastEvent,
    setLastEvent,
    userSettings,
    changeUserSettings,
    setConfirmationDialogData
}) => {
    const [openedCategoryContent, setOpenedCategoryContent] = useState({ children: [] });
    useEffect(() => processEvent(), [lastEvent]);
    useEffect(() => loadCategory(), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            loadCategory();
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
                loadCategory();
                break;
        }
    }

    const loadCategory = () => {
        const fetch = async () => {
            const listItemId = openedListItem.id;

            const queryParams = openedListItem.id === null ? null : { categoryId: openedListItem.id };
            const response = await NOTEBOOK_GET_CHILDREN_OF_CATEGORY.createRequest(null, null, queryParams)
                .send();

            if (openedListItem.id === listItemId) {
                setOpenedCategoryContent(response);
            }
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

        return new Stream(openedCategoryContent.children)
            .sorted((a, b) => compareListItems(a, b))
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

            <div
                id="notebook-content-category-content-navigation"
                onDrop={handleOnDrop}
                onDragOver={handleOnDragOver}
            >
                <div id="notebook-content-category-content-title"> {openedListItem.id === null ? localizationHandler.get("root") : openedCategoryContent.title} </div>

                <Button
                    id="notebook-content-category-content-up-button"
                    label={localizationHandler.get("up")}
                    onclick={() => setOpenedListItem({ id: openedCategoryContent.parent, type: OpenedPageType.CATEGORY })}
                    disabled={openedListItem.id === null}
                />
            </div>

            <div id="notebook-category-content-list">
                {getContent()}
            </div>
        </div>
    );
}

export default Category;