import React from "react";
import Button from "../../../../../../../common/component/input/Button";
import OpenedPageType from "../../../../../common/OpenedPageType";
import "./category_navigation.css";
import InputField from "../../../../../../../common/component/input/InputField";
import Stream from "../../../../../../../common/js/collection/Stream";
import localizationData from "./category_navigation_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import Event from "../../../../../../../common/js/event/Event";
import EventName from "../../../../../../../common/js/event/EventName";
import { NOTEBOOK_ARCHIVE_ITEM, NOTEBOOK_DELETE_LIST_ITEM, NOTEBOOK_PIN_LIST_ITEM } from "../../../../../../../common/js/dao/endpoints/NotebookEndpoints";
import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const CategoryNavigation = ({
    openedListItem,
    title,
    parent,
    setOpenedListItem,
    listItems,
    selectedItems,
    setSelectedItems,
    setLastEvent,
    setConfirmationDialogData,
    handleOnDrop = () => { },
    handleOnDragOver = () => { },
    setDisplaySpinner
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const unarchiveSelected = () => {
        setArchiveStatusSelected(false);
    }

    const archiveSelected = () => {
        setArchiveStatusSelected(true);
    }

    const setArchiveStatusSelected = async (status) => {
        for (let index in selectedItems) {
            await NOTEBOOK_ARCHIVE_ITEM.createRequest({ value: status }, { listItemId: selectedItems[index] })
                .send(setDisplaySpinner);
        }

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_ARCHIVED));
    }

    const pinSelected = () => {
        setPinStatusSelected(true);
    }

    const unpinSelected = () => {
        setPinStatusSelected(false);
    }

    const setPinStatusSelected = async (status) => {
        for (let index in selectedItems) {
            await NOTEBOOK_PIN_LIST_ITEM.createRequest({ value: status }, { listItemId: selectedItems[index] })
                .send(setDisplaySpinner);
        }

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_PINNED));
    }

    const confirmDeleteSelected = () => {
        if (!selectedItems.length) {
            return;
        }

        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-delete-selected-confirmation-dialog",
            localizationHandler.get("confirm-delete-selected-title"),
            localizationHandler.get("confirm-delete-selected-detail", { amount: selectedItems.length }),
            [
                <Button
                    key="delete"
                    id="notebook-delete-selected-items-confirm-button"
                    label={localizationHandler.get("delete-selected")}
                    onclick={deleteSelected}
                />,
                <Button
                    key="cancel"
                    id="notebook-delete-selected-items-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteSelected = async () => {
        for (let index in selectedItems) {
            await NOTEBOOK_DELETE_LIST_ITEM.createRequest(null, { listItemId: selectedItems[index] })
                .send(setDisplaySpinner);
        }


        setConfirmationDialogData(null);
        setSelectedItems([]);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_DELETED));
    }

    return (
        <div
            id="notebook-content-category-content-navigation"
            onDrop={handleOnDrop}
            onDragOver={handleOnDragOver}
        >
            <div id="notebook-content-category-content-title"> {openedListItem.id === null ? localizationHandler.get("root") : title} </div>

            <div id="notebook-content-category-content-operations">
                <div id="notebook-content-category-content-select-all-buttons">
                    <InputField
                        id="notebook-content-category-content-unselect-all-button"
                        type="checkbox"
                        checked={false}
                        onclickCallback={() => setSelectedItems([])}
                    />
                    <span>/</span>
                    <InputField
                        id="notebook-content-category-content-select-all-button"
                        type="checkbox"
                        checked={true}
                        onclickCallback={() => setSelectedItems(new Stream(listItems).map(item => item.id).toList())}
                    />
                </div>

                <Button
                    id="notebook-content-category-content-up-button"
                    label={localizationHandler.get("up")}
                    onclick={() => setOpenedListItem({ id: parent, type: OpenedPageType.CATEGORY })}
                    disabled={openedListItem.id === null}
                />

                <div id="notebook-content-category-content-bulk-operations">
                    <Button
                        id="notebook-content-category-content-archive-selected-button"
                        title={localizationHandler.get("archive-selected")}
                        onclick={archiveSelected}
                    />

                    <Button
                        id="notebook-content-category-content-unarchive-selected-button"
                        className={"disabled"}
                        title={localizationHandler.get("unarchive-selected")}
                        onclick={unarchiveSelected}
                    />

                    <Button
                        id="notebook-content-category-content-pin-selected-button"
                        title={localizationHandler.get("pin-selected")}
                        onclick={pinSelected}
                    />

                    <Button
                        id="notebook-content-category-content-unpin-selected-button"
                        title={localizationHandler.get("unpin-selected")}
                        onclick={unpinSelected}
                    />

                    <Button
                        id="notebook-content-category-content-delete-selected-button"
                        title={localizationHandler.get("delete-selected")}
                        onclick={confirmDeleteSelected}
                    />
                </div>
            </div>
        </div>
    );
}

export default CategoryNavigation;