import React from "react";
import Button from "../../../../../../common/component/input/Button";
import EventName from "../../../../../../common/js/event/EventName";
import Event from "../../../../../../common/js/event/Event";
import { NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP } from "../../../../../../common/js/dao/endpoints/NotebookEndpoints";

const PinnedItem = ({ listItem, localizationHandler, setLastEvent, pinGroupId, setItems, setDisplaySpinner }) => {
    const remove = async () => {
        const response = await NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP.createRequest(null, { pinGroupId: pinGroupId, listItemId: listItem.id })
            .send(setDisplaySpinner);

        setItems(response);
        setLastEvent(new Event(
            EventName.NOTEBOOK_PINNED_ITEM_MOVED,
            {
                pinGroupId: pinGroupId,
                items: response
            }
        ));
    }

    return (
        <div className={"notebook-pin-group-manager-pinned-item " + listItem.type.toLowerCase()}>
            <span>{listItem.title}</span>

            <div className="notebook-pin-group-manager-pinned-item-buttons">
                <Button
                    className={"notebook-pin-group-manager-pinned-item-remove-button"}
                    label={localizationHandler.get("remove-from-pin-group")}
                    onclick={remove}
                />
            </div>
        </div>
    );
}

export default PinnedItem;