import InputField from "./InputField";

const ValidatedInputField = ({
    validationResultId,
    className = "",
    inputId,
    inputClassName,
    validationResult,
    type,
    placeholder,
    onchangeCallback,
    value
}) => {
    const getValidationResultField = () => {
        return <div
            id={validationResultId}
            className={"validation-result-field" + " " + className}
            title={validationResult.message}
        >
            X
        </div>
    }

    return (
        <div className="validated-input-field-wrapper">
            <InputField
                className={inputClassName}
                id={inputId}
                type={type}
                placeholder={placeholder}
                onchangeCallback={onchangeCallback}
                value={value}
            />

            {validationResult && !validationResult.valid && getValidationResultField()}
        </div>
    );
}

export default ValidatedInputField;