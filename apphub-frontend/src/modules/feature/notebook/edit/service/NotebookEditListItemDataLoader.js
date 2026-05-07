import { NOTEBOOK_GET_LIST_ITEM } from "../../NotebookEndpoints";

const loadItemData = (listItemId, setDataFromResponse) => {
    const fetch = async () => {
        const response = await NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: listItemId })
            .send();

        setDataFromResponse(response);
    }
    fetch();
}

export default loadItemData;