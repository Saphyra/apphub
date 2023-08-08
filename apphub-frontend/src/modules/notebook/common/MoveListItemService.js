import Endpoints from "../../../common/js/dao/dao";
import Event from "../../../common/js/event/Event";
import EventName from "../../../common/js/event/EventName";

const moveListItem = async (listItemId, newParent, setLastEvent) => {
    if (listItemId === newParent) {
        return;
    }

    await Endpoints.NOTEBOOK_MOVE_LIST_ITEM.createRequest({ value: newParent }, { listItemId: listItemId })
        .send();

    setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED))
}

export default moveListItem;