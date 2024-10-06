import { NOTEBOOK_GET_LIST_ITEM } from "../../../../common/js/dao/endpoints/NotebookEndpoints";

const loadItemData = (listItemId, setDataFromResponse) => {
    const fetch = async () => {
        const response = await NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: listItemId })
            .send();

        setDataFromResponse(response);
    }
    fetch();
}

export default loadItemData;