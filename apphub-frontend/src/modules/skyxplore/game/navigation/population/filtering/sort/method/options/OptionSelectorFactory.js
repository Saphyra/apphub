import SelectInput, { SelectOption } from "../../../../../../../../../common/component/input/SelectInput";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../../../../common/js/collection/Stream";
import citizenLocalizationData from "../../../../../../common/localization/citizen_localization.json";

const getSelector = (id, value, source, onchangeCallback) => {
    const citizenLocalizationHandler = new LocalizationHandler(citizenLocalizationData);

    return (
        <SelectInput
            id={id}
            value={value}
            options={new Stream(source)
                .sorted((a, b) => citizenLocalizationHandler.get(a).localeCompare(citizenLocalizationHandler.get(b)))
                .map(item => new SelectOption(
                    citizenLocalizationHandler.get(item),
                    item
                ))
                .toList()
            }
            onchangeCallback={onchangeCallback}
        />
    );
}

export default getSelector;