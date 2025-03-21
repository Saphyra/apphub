import React from "react";
import OpenedPageType from "../../../common/OpenedPageType";
import "./list_item.css";
import Button from "../../../../../common/component/input/Button";
import Event from "../../../../../common/js/event/Event";
import EventName from "../../../../../common/js/event/EventName";
import ListItemMode from "./ListItemMode";
import Constants from "../../../../../common/js/Constants";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import moveListItem from "../../../common/MoveListItemService";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { addAndSet, removeAndSet, throwException } from "../../../../../common/js/Utils";
import { NOTEBOOK_ARCHIVE_ITEM, NOTEBOOK_CLONE_LIST_ITEM, NOTEBOOK_DELETE_LIST_ITEM, NOTEBOOK_PIN_LIST_ITEM } from "../../../../../common/js/dao/endpoints/NotebookEndpoints";
import InputField from "../../../../../common/component/input/InputField";
import Stream from "../../../../../common/js/collection/Stream";

const ListItem = ({ localizationHandler, data, setOpenedListItem, setLastEvent, listItemMode, setConfirmationDialogData, selectedItems, setSelectedItems }) => {
    const handleOnclick = () => {
        if (!data.enabled) {
            NotificationService.showError(localizationHandler.get("list-item-disabled"));
            return;
        }

        switch (data.type) {
            case OpenedPageType.CATEGORY:
            case OpenedPageType.TEXT:
            case OpenedPageType.CHECKLIST:
            case OpenedPageType.TABLE:
            case OpenedPageType.CUSTOM_TABLE:
            case OpenedPageType.CHECKLIST_TABLE:
            case OpenedPageType.IMAGE:
            case OpenedPageType.FILE:
                setOpenedListItem({ id: data.id, type: data.type });
                break;
            case OpenedPageType.LINK:
                window.open(data.value);
                break;
            case OpenedPageType.ONLY_TITLE:
                break;
            default:
                throwException("IllegalArgument", "ListItemType " + data.type + " is not handled in ListItem.");
        }
    }

    const confirmDeleteListItem = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-category-content-delete-list-item-confirmation-dialog",
            localizationHandler.get("confirm-list-item-deletion-title"),
            localizationHandler.get("confirm-list-item-deletion-content", { listItemTitle: data.title }),
            [
                <Button
                    key="delete"
                    id="notebook-content-category-content-delete-list-item-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteListItem}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-category-content-cancel-deletion-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_DELETED, data));
    }

    const deleteListItem = async () => {
        await NOTEBOOK_DELETE_LIST_ITEM.createRequest(null, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_DELETED, data));
        setConfirmationDialogData(null);
    }

    const setArchiveStatus = async (archived) => {
        const payload = {
            value: archived
        }

        await NOTEBOOK_ARCHIVE_ITEM.createRequest(payload, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_ARCHIVED, data));
    }

    const setPinStatus = async (pinned) => {
        const payload = {
            value: pinned
        }

        await NOTEBOOK_PIN_LIST_ITEM.createRequest(payload, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_PINNED, data));
    }

    const cloneListItem = async () => {
        await NOTEBOOK_CLONE_LIST_ITEM.createRequest(null, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_CLONED, data));
    }

    const archiveButton = () => {
        return <Button
            className="notebook-content-category-content-list-item-archive-button"
            onclick={() => setArchiveStatus(true)}
            title={localizationHandler.get("archive")}
        />
    }

    const unarchiveButton = () => {
        return <Button
            className="notebook-content-category-content-list-item-unarchive-button"
            onclick={() => setArchiveStatus(false)}
            title={localizationHandler.get("unarchive")}
        />
    }

    const pinButton = () => {
        return <Button
            className="notebook-content-category-content-list-item-pin-button"
            onclick={() => setPinStatus(true)}
            title={localizationHandler.get("pin")}
        />
    }

    const unpinButton = () => {
        return <Button
            className="notebook-content-category-content-list-item-unpin-button"
            onclick={() => setPinStatus(false)}
            title={localizationHandler.get("unpin")}
        />
    }

    //Drag & Drop
    const handleOnDragStart = (e) => {
        e.dataTransfer.setData("id", data.id);
    }

    const handleOnDragOver = (e) => {
        if (data.type === OpenedPageType.CATEGORY) {
            e.preventDefault();
        }
    }

    const handleOnDrop = (e) => {
        const movedItemId = e.dataTransfer.getData("id");
        moveListItem(movedItemId, data.id, setLastEvent);
    }

    const toggleSelected = (checked, e) => {
        e.stopPropagation();

        if (checked) {
            addAndSet(selectedItems, data.id, setSelectedItems);
        } else {
            removeAndSet(selectedItems, id => id == data.id, setSelectedItems);
        }
    }

    return (
        <div
            id={data.id}
            className={"button notebook-content-category-content-list-item " + (data.type == OpenedPageType.ONLY_TITLE || !data.enabled ? " disabled " : "") + data.type.toLowerCase() + (data.pinned ? " pinned" : "") + (data.archived ? " archived" : "")}
            draggable
            onDragStart={handleOnDragStart}
            onDrop={handleOnDrop}
            onDragOver={handleOnDragOver}
            onClick={handleOnclick}
        >
            {listItemMode !== ListItemMode.PINNED_ITEM &&
                <InputField
                    className="notebook-content-category-content-list-item-selected"
                    type="checkbox"
                    checked={new Stream(selectedItems).anyMatch(id => id === data.id)}
                    onchangeCallback={toggleSelected}
                    onclickCallback={e => e.stopPropagation()}
                />
            }
            <span className="notebook-content-category-content-list-item-title">{data.title}</span>

            <div className="notebook-content-category-content-list-item-buttons">
                {listItemMode !== ListItemMode.PINNED_ITEM &&
                    <Button
                        className="notebook-content-category-content-list-item-delete-button"
                        onclick={confirmDeleteListItem}
                        title={localizationHandler.get("delete")}
                    />
                }

                {listItemMode !== ListItemMode.PINNED_ITEM && (data.archived ? unarchiveButton() : archiveButton())}

                {data.pinned ? unpinButton() : pinButton()}

                {listItemMode !== ListItemMode.CATEGORY_CONTENT &&
                    <Button
                        className="notebook-content-category-content-list-item-parent-button"
                        onclick={() => setOpenedListItem({ id: data.parentId, type: OpenedPageType.CATEGORY })}
                        title={localizationHandler.get("open-parent")}
                    />
                }

                {listItemMode !== ListItemMode.PINNED_ITEM &&
                    <Button
                        className="notebook-content-category-content-list-item-clone-button"
                        onclick={() => cloneListItem()}
                        title={localizationHandler.get("clone")}
                    />
                }

                {listItemMode !== ListItemMode.PINNED_ITEM &&
                    <Button
                        className="notebook-content-category-content-list-item-edit-button"
                        onclick={() => window.location.href = Constants.NOTEBOOK_EDIT_PAGE + "/" + data.id}
                        title={localizationHandler.get("edit")}
                    />
                }
            </div>
        </div>
    );
}

export default ListItem;