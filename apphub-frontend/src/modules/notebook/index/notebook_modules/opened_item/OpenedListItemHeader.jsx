import React from "react";
import ListItemTitle from "../../../common/list_item_title/ListItemTitle";
import Button from "../../../../../common/component/input/Button";

const OpenedListItemHeader = ({
    localizationHandler,
    title,
    setTitle,
    editingEnabled,
    close
}) => {
    return (
        <ListItemTitle
            id="notebook-content-list-item-title"
            inputId="notebook-content-list-item-title-input"
            placeholder={localizationHandler.get("list-item-title")}
            value={title}
            setListItemTitle={setTitle}
            disabled={!editingEnabled}
            closeButton={
                <Button
                    id="notebook-content-list-item-close-button"
                    className="notebook-close-button"
                    label="X"
                    onclick={close}
                />
            }
        />
    );
}

export default OpenedListItemHeader;