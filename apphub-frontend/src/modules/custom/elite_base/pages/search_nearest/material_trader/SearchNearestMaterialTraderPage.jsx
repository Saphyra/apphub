import React, { useEffect, useState } from "react";
import StarSelector from "../../../common/component/star_selector/StarSelector";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "../../../../../../common/js/Utils";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../../common/js/collection/MapStream";
import MaterialType, { ANY_MATERIAL_TYPE } from "./MaterialType";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import localizationData from "./search_nearest_material_trader_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Button from "../../../../../../common/component/input/Button";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import { ELITE_BASE_NEAREST_MATERIAL_TRADERS } from "../../../common/EliteBaseEndpoints";
import SearchNearestMaterialTraderResult from "./SearchNearestMaterialTraderResult";
import Stream from "../../../../../../common/js/collection/Stream";
import "./search_nearest_material_trader.css";

const SearchNearestMaterialTraderPage = ({ }) => {
    const CACHE_KEY_MATERIAL_TYPE = "eliteBaseNearestMaterialTraderMaterialType";

    const localizationHandler = new LocalizationHandler(localizationData);

    const [starId, setStarId] = useState(null);
    const [materialType, setMaterialType] = useState(cachedOrDefault(CACHE_KEY_MATERIAL_TYPE, ANY_MATERIAL_TYPE));
    const [searchResult, setSearchResult] = useState([]);
    const [page, setPage] = useState(0);

    useEffect(() => setPage(0), [starId, materialType]);
    useEffect(() => setSearchResult([]), [starId, materialType]);

    return (
        <div className="elite-base-page">
            <fieldset>
                <legend>{localizationHandler.get("parameters")}</legend>

                <StarSelector
                    starId={starId}
                    setStarId={setStarId}
                />

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("material-type")}
                        input={<SelectInput
                            id="elite-base-sn-material-trader-select-mattype"
                            value={materialType}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MATERIAL_TYPE, v, setMaterialType)}
                            options={getOptions()}
                        />}
                    />
                </div>
            </fieldset>

            {searchResult.length > 0 &&
                <SearchNearestMaterialTraderResult
                    localizationHandler={localizationHandler}
                    searchResult={searchResult}
                    reload={reload}
                />
            }

            <div id="elite-base-sn-material-trader-search-button-wrapper">
                <Button
                    id="elite-base-sn-material-trader-search-button"
                    label={page == 0 ? localizationHandler.get("search") : localizationHandler.get("load-more")}
                    onclick={() => search(page)}
                />
            </div>
        </div>
    );

    function reload() {
        setPage(0);
        search(0, true);
    }

    function getOptions() {
        return new MapStream(MaterialType)
            .toListStream()
            .map(materialType => new SelectOption(materialType, materialType))
            .add(new SelectOption(ANY_MATERIAL_TYPE, ANY_MATERIAL_TYPE))
            .toList();
    }

    async function search(page, eraseExisting = false) {
        if (!hasValue(starId)) {
            NotificationService.showError(localizationHandler.get("star-not-selected"));
            return;
        }

        const response = await ELITE_BASE_NEAREST_MATERIAL_TRADERS.createRequest(null, { starId: starId, materialType: materialType, page: page })
            .send();

        if (response.length == 0) {
            NotificationService.showError(localizationHandler.get("no-more-result"));
            return;
        }

        setPage(page + 1);
        if (eraseExisting) {
            setSearchResult(response);
        } else {
            setSearchResult(new Stream(searchResult).addAll(response).toList());
        }
    }
}

export default SearchNearestMaterialTraderPage;