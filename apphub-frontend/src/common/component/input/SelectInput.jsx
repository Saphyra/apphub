import React from "react";
import Stream from "../../js/collection/Stream";
import InputField from "./InputField";
import PostLabeledInputField from "./PostLabeledInputField";
import { addAndSet, removeAndSet } from "../../js/Utils";

const SelectInput = ({
    id,
    className,
    value,
    options = [],
    onchangeCallback,
    disabled = false
}) => {

    const getOptions = () => {
        return new Stream(options)
            .map((option, index) =>
                <option
                    key={index}
                    value={option.value}
                >
                    {option.label}
                </option>
            )
            .toList();
    }

    const onchange = (e) => {
        onchangeCallback(e.target.value, e);
    }

    return (
        <select
            id={id}
            className={className}
            onChange={onchange}
            value={value}
            disabled={disabled}
        >
            {getOptions()}
        </select>
    );
}

export const MultiSelect = ({
    id,
    className,
    value,
    options = [],
    onchangeCallback,
    disabled = false
}) => {
    return (
        <div
            id={id}
            className={"multi-select " + className}
        >
            {!disabled &&
                <div className="multi-select-operations">
                    <InputField
                        type="checkbox"
                        checked={false}
                        onchangeCallback={(t, e) => e.preventDefault()}
                        onclickCallback={() => onchangeCallback([])}
                    />
                    <span> / </span>
                    <InputField
                        type="checkbox"
                        checked={true}
                        onchangeCallback={(t, e) => e.preventDefault()}
                        onclickCallback={() => onchangeCallback(new Stream(options).map(o => o.value).toList())}
                    />
                </div>
            }

            <div className="multi-select-options">
                {getOptions()}
            </div>
        </div>
    );

    function getOptions() {
        return new Stream(options)
            .map((option, index) => <MultiSelectOption
                key={index}
                option={option}
                value={value}
                onchangeCallback={onchangeCallback}
                disabled={disabled}
            />)
            .toList();
    }


}

const MultiSelectOption = ({ option, value, onchangeCallback, disabled }) => {
    return (
        <div className="multi-select-option">
            <PostLabeledInputField
                label={option.label}
                input={<InputField
                    type="checkbox"
                    disabled={disabled}
                    onchangeCallback={handleOnchange}
                    checked={new Stream(value).anyMatch(v => v === option.value)}
                />
                }
            />
        </div>
    );

    function handleOnchange(checked) {
        if (checked) {
            addAndSet(value, option.value, onchangeCallback);
        } else {
            removeAndSet(value, v => v == option.value, onchangeCallback)
        }
    }
}

export const SelectOption = class {
    constructor(label, value) {
        this.label = label;
        this.value = value;
    }
}

export default SelectInput;