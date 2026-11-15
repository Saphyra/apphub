import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import Optional from "common/js/collection/Optional";
import NotificationService from "common/js/notification/NotificationService";

const BoundarySelector = ({ boundary, setBoundary, label, validation = () => new Optional(null) }) => {
    const validateAndUpdate = (value) => {
        const validationResult = validation(value);

        if (validationResult.isPresent()) {
            const errorMessage = validationResult.get();

            NotificationService.showError(errorMessage);
        } else {
            setBoundary(value);
        }
    }

    return (
        <fieldset className="monitoring-inputs-boundary inline-block" style={{ padding: "3px" }}>
            <span>{label}</span>
            <InputField
                type="datetime-local"
                value={boundary}
                onchangeCallback={validateAndUpdate}
            />
            <Button
                label="X"
                onclick={() => setBoundary("")}
            />
        </fieldset>
    );
}

export default BoundarySelector;