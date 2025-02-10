import React, { useEffect, useState } from "react";
import LabelWrappedInputField from "../../../../../../common/component/input/LabelWrappedInputField";
import localizationData from "./star_selector_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import DataListInputField, { DataListInputEntry } from "../../../../../../common/component/input/DataListInputField";
import { hasValue } from "../../../../../../common/js/Utils";
import { ELITE_BASE_STAR_SYSTEMS_SEARCH } from "../../EliteBaseEndpoints";
import MapStream from "../../../../../../common/js/collection/MapStream";
import Stream from "../../../../../../common/js/collection/Stream";
import "./star_selector.css";

const StarSelector = ({ starId, setStarId, locked }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [selectedStar, setSelectedStar] = useState(new DataListInputEntry(starId, ""));
    const [queryResult, setQueryResult] = useState([]);
    const [timeout, sTimeout] = useState(null);

    useEffect(updateStarId, [selectedStar, queryResult]);
    useEffect(scheduleQueryStars, [selectedStar]);

    return (
        <div>
            <LabelWrappedInputField
                preLabel={localizationHandler.get("pre-label")}
                postLabel={localizationHandler.get("post-label")}
                inputField={<DataListInputField
                    className={"elite-base-star-selector" + (locked ? " locked" : "")}
                    value={selectedStar}
                    setValue={setSelectedStar}
                    options={queryResult}
                    placeholder={localizationHandler.get("reference-system")}
                    onKeyUp={() => setStarId(null)}
                />
                }
            />
        </div>
    );

    function scheduleQueryStars() {
        if (hasValue(timeout)) {
            clearTimeout(timeout);
        }

        sTimeout(setTimeout(queryStars, 1000));
    }

    function queryStars() {
        fetch();

        async function fetch() {
            if (selectedStar.value.length < 3) {
                return;
            }

            if (hasValue(selectedStar.key)) {
                return;
            }

            const response = await ELITE_BASE_STAR_SYSTEMS_SEARCH.createRequest({ value: selectedStar.value })
                .send();

            const mapped = new MapStream(response)
                .toList((key, value) => new DataListInputEntry(key, value));

            setQueryResult(mapped);
        }
    }

    function updateStarId() {
        if (hasValue(selectedStar.key)) {
            setStarId(selectedStar.key);
        } else {
            new Stream(queryResult)
                .filter(star => star.value.toLowerCase() === selectedStar.value.toLowerCase())
                .findFirst()
                .ifPresent(star => {
                    setStarId(star.key);
                    setSelectedStar(star);
                });
        }
    }
}

export default StarSelector;