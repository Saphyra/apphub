import InputField from "./InputField";

const ValidatedInputField = ({ validationResult, type, placeholder, onchangeCallback, value }) => {
    const getValidationResultField = () => {
        return <div
            className="validation-result-field"
            title={validationResult.message}
        >
            X
        </div>
    }

    return (
        <div className="validated-input-field-wrapper">
            <InputField
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