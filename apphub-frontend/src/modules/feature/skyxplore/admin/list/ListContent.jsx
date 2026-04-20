import Stream from "common/js/collection/Stream";
import ItemData from "../common/ItemData";
import Button from "common/component/input/Button";
import { SKYXPLORE_ADMIN_DETAILS_PAGE } from "common/js/dao/endpoints/skyxplore/SkyXploreAdminEndpoints";
import { hasValue } from "common/js/Utils";

const ListContent = ({ items, filters, gameId, type }) => {
    return (
        <div id="skyxplore-admin-list-content">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(items)
            .filter(matches)
            .map(item => <ItemData
                key={item.id}
                id={item.id}
                data={item.data}
                operations={<Button
                    label="Details"
                    onclick={() => window.open(SKYXPLORE_ADMIN_DETAILS_PAGE.assembleUrl({ gameId: hasValue(gameId) ? gameId : item.id, id: item.id, type: type }))}
                />}
            />)
            .toList();

        function matches(item) {
            const data = item.data;

            for (const property in filters) {
                if (data[property] == null) {
                    console.log("Property " + property + " not found in item " + item.id);
                    return false;
                }

                const search = filters[property].toLowerCase();
                const value = data[property].toString().toLowerCase();

                if (!value.includes(search)) {
                    return false;
                }
            }

            return true;
        }
    }
}



export default ListContent;