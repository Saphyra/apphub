import React, { useState } from "react";
import Stream from "../../js/collection/Stream";
import { generateRandomId } from "../../js/Utils";

const DataListInputField = ({
    id,
    className,
    value = new DataListInputEntry(),
    setValue,
    options = [],
    placeholder,
    onKeyUp = () =>{}
}) => {
    const [dataListId] = useState(generateRandomId());

    const updateValue = (v) => {
        const input = v.target.value;

        const maybePreDefinedValue = new Stream(options)
            .filter(option => option.value === input)
            .findFirst();

        const result = new DataListInputEntry(
            maybePreDefinedValue.isPresent() ? maybePreDefinedValue.get().key : null,
            input
        );

        setValue(result);
    }

    const getOptions = () => {
        return new Stream(options)
            .map(entry => (
                <option key={entry.key}>
                    {entry.value}
                </option>
            ))
            .toList();
    }

    return (
        <span>
            <input
                id={id}
                className={className}
                value={value.value}
                onChange={updateValue}
                onSelect={updateValue}
                list={dataListId}
                placeholder={placeholder}
                onKeyUp={onKeyUp}
            />
            <datalist id={dataListId}>
                {getOptions()}
            </datalist>
        </span>
    );
}

export const DataListInputEntry = class {
    constructor(key = null, value = "") {
        this.key = key;
        this.value = value;
    }
}

export default DataListInputField;