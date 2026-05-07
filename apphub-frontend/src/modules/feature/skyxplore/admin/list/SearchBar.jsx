import InputField from "common/component/input/InputField";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import Stream from "common/js/collection/Stream";
import { useEffect, useState } from "react";

const SearchBar = ({ items, filters, setFilters }) => {
    const [properties, setProperties] = useState([]);

    useEffect(collectProperties, [items]);

    return (
        <div id="skyxplore-admin-list-search-bar">
            {getContent()}
        </div>
    );

    function collectProperties() {
        const pr = new Stream(items)
            .flatMap(item => new Stream(Object.keys(item.data)))
            .distinct()
            .toList();

        setProperties(pr);
    }

    function getContent() {
        return new Stream(properties)
            .map(property => <SearchInput
                key={property}
                property={property}
                filters={filters}
                setFilters={setFilters}
            />)
            .toList();
    }
}

const SearchInput = ({ property, filters, setFilters }) => {
    const [value, setValue] = useState(filters[property] || "");

    useEffect(
        () => {
            if (value === "") {
                const copy = { ...filters };
                delete copy[property];
                setFilters(copy);
            } else {
                setFilters({ ...filters, [property]: value });
            }
        },
        [value]
    );

    return <PreLabeledInputField
        label={property}
        input={<InputField
            value={value}
            onchangeCallback={setValue}
            placeholder={property}
        />}
    />
}

export default SearchBar;