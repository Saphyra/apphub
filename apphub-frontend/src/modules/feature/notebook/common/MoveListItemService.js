import EventName from "common/js/event/EventName";
import { NOTEBOOK_MOVE_LIST_ITEM } from "../NotebookEndpoints";
import Event from "common/js/event/Event";

const moveListItem = async (listItemId, newParent, setLastEvent, setDisplaySpinner) => {
    if (listItemId === newParent) {
        return;
    }

    await NOTEBOOK_MOVE_LIST_ITEM.createRequest({ value: newParent }, { listItemId: listItemId })
        .send(setDisplaySpinner);

    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED))
}

export default moveListItem;