import React, { useEffect, useState } from "react";
import Textarea from "../../../../../../common/component/input/Textarea";
import Endpoints from "../../../../../../common/js/dao/dao";
import Button from "../../../../../../common/component/input/Button";
import OpenedPageType from "../../../../common/OpenedPageType";
import "./text.css";
import validateListItemTitle from "../../../../common/validator/ListItemTitleValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import EventName from "../../../../../../common/js/event/EventName";
import Event from "../../../../../../common/js/event/Event";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { useUpdateEffect } from "react-use";
import OpenedListItemHeader from "../OpenedListItemHeader";
import useHasFocus from "../../../../../../common/hook/UseHasFocus";

const Text = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    useEffect(() => loadText(), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadText();
        }
    }, [isInFocus]);

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

    const close = () => {
        if (editingEnabled) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "notebook-content-text-close-confirmation",
                localizationHandler.get("confirm-close-title"),
                localizationHandler.get("confirm-close-content"),
                [
                    <Button
                        key="close"
                        id="notebook-content-text-close-confirm-button"
                        label={localizationHandler.get("close")}
                        onclick={doClose}
                    />,
                    <Button
                        key="cancel"
                        id="notebook-content-text-close-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));
        } else {
            doClose();
        }
    }

    const doClose = () => {
        setOpenedListItem({ id: parent, type: OpenedPageType.CATEGORY })
        setConfirmationDialogData(null);
    }

    return (
        <div id="notebook-content-text" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={editingEnabled}
                close={close}
            />

            <Textarea
                id="notebook-content-text-content"
                className="notebook-content-view-main"
                placeholder={localizationHandler.get("content")}
                value={content}
                disabled={!editingEnabled}
                onchangeCallback={setContent}
                onKeyDownCallback={e => {
                    if (e.key === "Tab") {
                        e.preventDefault();

                        const start = e.target.selectionStart;
                        const end = e.target.selectionEnd;
                        e.target.value = e.target.value.substring(0, start) + "    " + e.target.value.substring(end);
                        e.target.selectionStart = e.target.selectionEnd = start + 4;
                    }
                }}
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
