import Endpoints from "../../../../common/js/dao/dao";

const loadItemData = (listItemId, setDataFromResponse) => {
    const fetch = async () => {
        const response = await Endpoints.NOTEBOOK_GET_LIST_ITEM.createRequest(null, { listItemId: listItemId })
            .send();

        setDataFromResponse(response);
    }
    fetch();
}

export default loadItemData;