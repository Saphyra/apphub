import React, { useEffect, useState } from "react";
import useCache from "../../../../../../../common/hook/Cache";
import CacheKey from "../../../../common/constants/CacheKey";
import "./storage_setting_creator.css";
import localizationData from "./storage_setting_creator_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../../../common/component/input/SelectInput";
import Stream from "../../../../../../../common/js/collection/Stream";
import resourceLocalizationData from "../../../../common/localization/resource_localization.json";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import RangeInput from "../../../../../../../common/component/input/RangeInput";
import Button from "../../../../../../../common/component/input/Button";
import { isBlank } from "../../../../../../../common/js/Utils";
import { SKYXPLORE_DATA_RESOURCES } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import { SKYXPLORE_PLANET_CREATE_STORAGE_SETTING } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const StorageSettingCreator = ({ planetId, storageSettings, setStorageSettings }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const [commodity, setCommodity] = useState("");
    const [amount, setAmount] = useState(1);
    const [priority, setPriority] = useState(5);

    const [commodities, setCommodities] = useState([]);
    const [availableCommodities, setAvailableCommodities] = useState([]);

    useCache(CacheKey.RESOURCE_DATA_IDS, SKYXPLORE_DATA_RESOURCES.createRequest(), setCommodities);

    useEffect(() => getAvailableCommodities(), [commodities, storageSettings]);

    const getAvailableCommodities = () => {
        const result = new Stream(commodities)
            .filter(resource => !settingAlreadyExists(resource))
            .toList();

        setAvailableCommodities(result);
    }

    const getCommodityOptions = () => {
        return new Stream(availableCommodities)
            .sorted((a, b) => resourceLocalizationHandler.get(a).localeCompare(resourceLocalizationHandler.get(b)))
            .map(resource => new SelectOption(
                resourceLocalizationHandler.get(resource),
                resource
            ))
            .add(new SelectOption("", ""))
            .toList();
    }

    const settingAlreadyExists = (resource) => {
        return new Stream(storageSettings)
            .anyMatch(storageSetting => storageSetting.dataId === resource);
    }

    const createStorageSetting = async () => {
        const paylaod = {
            dataId: commodity,
            targetAmount: amount,
            priority: priority
        }

        const response = await SKYXPLORE_PLANET_CREATE_STORAGE_SETTING.createRequest(paylaod, { planetId: planetId })
            .send();
        setStorageSettings(response);
    }

    return (
        <div id="skyxplore-game-storage-setting-creator">
            <div id="skyxplore-game-storage-setting-creator-title">{localizationHandler.get("title")}</div>

            <div id="skyxplore-game-storage-setting-creator-content">
                <PreLabeledInputField
                    label={localizationHandler.get("commodity") + ":"}
                    input={<SelectInput
                        id="skyxplore-game-storage-setting-creator-commodity"
                        value={commodity}
                        options={getCommodityOptions()}
                        onchangeCallback={setCommodity}
                        disabled={availableCommodities.length === 0}
                    />}
                />

                <PreLabeledInputField
                    label={localizationHandler.get("amount") + ":"}
                    input={<NumberInput
                        id="skyxplore-game-storage-setting-creator-amount"
                        onchangeCallback={setAmount}
                        value={amount}
                        min={1}
                        disabled={availableCommodities.length === 0}
                    />}
                />

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("priority") + ":"}
                    postLabel={priority}
                    inputField={<RangeInput
                        id="skyxplore-game-storage-setting-creator-priority"
                        min={1}
                        max={10}
                        onchangeCallback={setPriority}
                        value={priority}
                        disabled={availableCommodities.length === 0}
                    />}
                />

                <Button
                    id="skyxplore-game-storage-setting-create-button"
                    label={localizationHandler.get("create")}
                    onclick={createStorageSetting}
                    disabled={availableCommodities.length === 0 || isBlank(commodity)}
                />
            </div>
        </div>
    );
}

export default StorageSettingCreator;