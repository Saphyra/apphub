import React from "react";
import MapStream from "../../../../../../../../common/js/collection/MapStream";
import { CitizenComparatorName } from "../CitizenComparator";
import SelectInput, { SelectOption } from "../../../../../../../../common/component/input/SelectInput";

const MethodSelect = ({ localizationHandler, citizenComparator, updateComparator }) => {
    const getOptions = () => {
        return new MapStream(CitizenComparatorName)
            .toListStream()
            .map(comparatorName => new SelectOption(
                localizationHandler.get(comparatorName),
                comparatorName
            ))
            .toList();
    }

    return <SelectInput
        id="skyxplore-game-population-comparator-selector"
        value={citizenComparator.name}
        options={getOptions()}
        onchangeCallback={updateComparator}
    />
}

export default MethodSelect;