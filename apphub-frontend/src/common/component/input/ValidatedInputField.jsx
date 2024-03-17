const ValidatedInputField = ({
    validationResultId,
    validationResultClassName = "",
    inputField,
    validationResult
}) => {
    const getValidationResultField = () => {
        return <div
            id={validationResultId}
            className={"validation-result-field" + " " + validationResultClassName}
            title={validationResult.message}
        >
            X
        </div>
    }

    return (
        <div className="validated-input-field-wrapper">
            {inputField}

            {validationResult && !validationResult.valid && getValidationResultField()}
        </div>
    );
}

export default ValidatedInputField;