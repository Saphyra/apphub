import SelectInput, { SelectOption } from "common/component/input/SelectInput";
import skillTypeLocalizationData from "../../../../../../common/localization/skill_type_localization.json";
import Stream from "common/js/collection/Stream";
import LocalizationHandler from "common/js/LocalizationHandler";

const getSelector = (id, value, source, onchangeCallback) => {
    const skillTypeLocalizationHandler = new LocalizationHandler(skillTypeLocalizationData);

    return (
        <SelectInput
            id={id}
            value={value}
            options={new Stream(source)
                .sorted((a, b) => skillTypeLocalizationHandler.get(a).localeCompare(skillTypeLocalizationHandler.get(b)))
                .map(item => new SelectOption(
                    skillTypeLocalizationHandler.get(item),
                    item
                ))
                .toList()
            }
            onchangeCallback={onchangeCallback}
        />
    );
}

export default getSelector;