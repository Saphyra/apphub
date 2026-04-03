import MapStream from "../../../../common/js/collection/MapStream";

const ItemData = ({ id, data, operations }) => {
    return (
        <fieldset
            className="skyxplore-admin-item-data selectable"
        >
            <legend>{id}</legend>

            {getContent()}

            {operations}
        </fieldset>
    );

    function getContent() {
        return new MapStream(data)
            .map((property, value) =>
                <div key={property}>
                    <span>{property}</span>
                    <span>: </span>
                    <span>{parse(value)}</span>
                </div>
            )
            .toList();

        function parse(value) {
            if (value == null) {
                return "null";
            }

            return value.toString();
        }
    }
}
export default ItemData;