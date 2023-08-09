import React, { useEffect, useState } from "react";
import ListItemTitle from "../../../../common/list_item_title/ListItemTitle";
import Textarea from "../../../../../../common/component/input/Textarea";
import Endpoints from "../../../../../../common/js/dao/dao";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import "./text.css";
import validateListItemTitle from "../../../../common/validator/ListItemTitleValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import EventName from "../../../../../../common/js/event/EventName";
import Event from "../../../../../../common/js/event/Event";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const Text = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    useEffect(() => loadText(), [openedListItem]);

    const loadText = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_TEXT.createRequest(null, { listItemId: openedListItem.id })
                .send();
            setTitle(response.title);
            setContent(response.content);
            setParent(response.parent);
        }
        fetch();
    }

    const discard = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-checklist-discard-confirmation",
            localizationHandler.get("confirm-discard-title"),
            localizationHandler.get("confirm-discard-content"),
            [
                <Button
                    key="discard"
                    id="notebook-content-checklist-discard-confirm-button"
                    label={localizationHandler.get("discard")}
                    onclick={() => {
                        setEditingEnabled(false);
                        loadText();
                        setConfirmationDialogData(null);
                    }}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-checklist-discard-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const save = async () => {
        const result = validateListItemTitle(title);
        if (!result.valid) {
            NotificationService.showError(result.message);
            return;
        }

        const payload = {
            title: title,
            content: content
        }

        const response = await Endpoints.NOTEBOOK_EDIT_TEXT.createRequest(payload, { listItemId: openedListItem.id })
            .send();

        setEditingEnabled(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
    }

    return (
        <div id="notebook-content-text" className="notebook-content notebook-content-view">
            <ListItemTitle
                id="notebook-content-text-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-text-close-button"
                        className="notebook-close-button"
                        label="X"
                        onclick={() => setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })}
                    />
                }
            />

            <Textarea
                id="notebook-content-text-content"
                className="notebook-content-view-main"
                placeholder={localizationHandler.get("content")}
                value={content}
                disabled={!editingEnabled}
                onchangeCallback={setContent}
            />

            <div className="notebook-content-buttons">
                {!editingEnabled &&
                    <Button
                        id="notebook-content-text-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-text-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-text-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                }
            </div>
        </div>
    )
}

export default Text;
