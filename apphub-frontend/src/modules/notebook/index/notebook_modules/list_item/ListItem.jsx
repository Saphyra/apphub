import React, { useState } from "react";
import ListItemType from "../../../common/ListItemType";
import Utils from "../../../../../common/js/Utils";
import "./list_item.css";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../../common/component/ConfirmationDialog";
import Endpoints from "../../../../../common/js/dao/dao";
import Event from "../../../../../common/js/event/Event";
import EventName from "../../../../../common/js/event/EventName";
import ListItemMode from "./ListItemMode";
import Constants from "../../../../../common/js/Constants";

const ListItem = ({ localizationHandler, data, setOpenedListItem, setLastEvent, listItemMode }) => {
    const [displayDeleteListItemConfirmationDialog, setDisplayDeleteListItemConfirmationDialog] = useState(false);

    console.log(data); //TODO remove

    const handleOnclick = () => {
        switch (data.type) {
            case ListItemType.CATEGORY:
            case ListItemType.TEXT:
            case ListItemType.CHECKLIST:
                setOpenedListItem({ id: data.id, type: data.type });
                break;
            case ListItemType.LINK:
                window.open(data.value);
                break;
            case ListItemType.ONLY_TITLE:
                break;
            default:
                Utils.throwException("IllegalArgument", "ListItemType " + data.type + " is not handled.");
        }
    }

    const deleteListItem = async () => {
        await Endpoints.NOTEBOOK_DELETE_LIST_ITEM.createRequest(null, { listItemId: data.id })
            .send();

        setDisplayDeleteListItemConfirmationDialog(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_DELETED, data));
    }

    const setArchiveStatus = async (archived) => {
        const payload = {
            value: archived
        }

        await Endpoints.NOTEBOOK_ARCHIVE_ITEM.createRequest(payload, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_ARCHIVED, data));
    }

    const setPinStatus = async (pinned) => {
        const payload = {
            value: pinned
        }

        await Endpoints.NOTEBOOK_PIN_LIST_ITEM.createRequest(payload, { listItemId: data.id })
            .send();

        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_PINNED, data));
    }

    const cloneListItem = async () => {
        await Endpoints.NOTEBOOK_CLONE_LIST_ITEM.createRequest(null, { listItemId: data.id })
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

    return (
        <div>
            <div
                id={data.id}
                className={"button notebook-content-category-content-list-item " + data.type.toLowerCase() + (data.archived ? " archived" : "" + (data.type == ListItemType.ONLY_TITLE ? " disabled" : ""))}
                onClick={handleOnclick}
            >
                <span className="notebook-content-category-content-list-item-title">{data.title}</span>

                <div className="notebook-content-category-content-list-item-buttons">
                    {listItemMode !== ListItemMode.PINNED_ITEM &&
                        <Button
                            className="notebook-content-category-content-list-item-delete-button"
                            onclick={() => setDisplayDeleteListItemConfirmationDialog(true)}
                            title={localizationHandler.get("delete")}
                        />
                    }

                    {listItemMode !== ListItemMode.PINNED_ITEM && (data.archived ? unarchiveButton() : archiveButton())}

                    {data.pinned ? unpinButton() : pinButton()}

                    {listItemMode !== ListItemMode.CATEGORY_CONTENT &&
                        <Button
                            className="notebook-content-category-content-list-item-parent-button"
                            onclick={() => setOpenedListItem({ id: data.parentId, type: ListItemType.CATEGORY })}
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

            {displayDeleteListItemConfirmationDialog &&
                <ConfirmationDialog
                    id="notebook-content-category-content-delete-list-item-confirmation-dialog"
                    title={localizationHandler.get("confirm-list-item-deletion-title")}
                    content={localizationHandler.get("confirm-list-item-deletion-content", { listItemTitle: data.title })}
                    choices={[
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
                            onclick={() => setDisplayDeleteListItemConfirmationDialog(false)}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default ListItem;