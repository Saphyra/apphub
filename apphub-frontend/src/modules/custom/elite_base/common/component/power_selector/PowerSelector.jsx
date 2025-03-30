import React, { useState } from "react";
import localizationData from "./power_selector_localization.json";
import SelectInput, { MultiSelect, SelectOption } from "../../../../../../common/component/input/SelectInput";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../common/js/collection/MapStream";
import PowerRelation from "./PowerRelation";
import useCache from "../../../../../../common/hook/Cache";
import Stream from "../../../../../../common/js/collection/Stream";
import { ELITE_BASE_GET_POWERS } from "../../EliteBaseEndpoints";
import "./power_selector.css";
import PowerNames from "../../localization/PowerNames";

const PowerSelector = ({ label, relation, setRelation, selectedPowers, setSelectedPowers }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [powers, setPowers] = useState([]);

    useCache("elite-base-powers", ELITE_BASE_GET_POWERS.createRequest(), setPowers);

    return (
        <div className="elite-base-power-selector">
            <div>
                <span>{label}: </span>

                <SelectInput
                    value={relation}
                    onchangeCallback={setRelation}
                    options={relationOptions()}
                />
            </div>

            {relation !== PowerRelation.ANY && relation != PowerRelation.EMPTY &&
                <MultiSelect
                    value={selectedPowers}
                    onchangeCallback={setSelectedPowers}
                    options={new Stream(powers).map(power => new SelectOption(PowerNames[power], power)).toList()}
                />
            }
        </div>
    );

    function relationOptions() {
        return new MapStream(PowerRelation)
            .toListStream()
            .map(r => new SelectOption(localizationHandler.get(r), r))
            .toList();
    }
}

export default PowerSelector;